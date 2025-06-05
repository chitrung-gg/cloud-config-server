package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.dto.authentication.AuthenticationRequestDto;
import com.viettel.spring.cloud.server.dto.authentication.AuthenticationResponseDto;
import com.viettel.spring.cloud.server.dto.authentication.RegisterRequestDto;
import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.mapper.AuthenticationMapper;
import com.viettel.spring.cloud.server.mapper.CustomUserDetailsMapper;
import com.viettel.spring.cloud.server.repository.UserRepository;
import com.viettel.spring.cloud.server.security.JwtTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${JWT_EXPIRY_TIME}")
    private Long expiryTime;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final AuthenticationMapper authenticationMapper;

    @Autowired
    private final CustomUserDetailsMapper customUserDetailsMapper;

    @Autowired
    private final JwtTokenService jwtTokenService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    // @Autowired
    // private final CriteriaQueryHelper criteriaQueryHelper;

    // @Autowired
    // private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Optional<AuthenticationResponseDto> login(AuthenticationRequestDto authenticationRequestDto) {
        UserEntity userEntity = userRepository.findByUsernameOrEmail(authenticationRequestDto.getUsernameOrEmail(), authenticationRequestDto.getUsernameOrEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username/email (DEBUGGING !)"));

        if (!passwordEncoder.matches(authenticationRequestDto.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException("Invalid password (DEBUGGING)!");
        }

        String token = jwtTokenService.generateToken(customUserDetailsMapper.convertEntityToCustomDetails(userEntity));

        logger.info("Token generated for " + userEntity.getUsername());
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();

        authenticationResponseDto.setAccessToken(token);
        authenticationResponseDto.setExpiryTime(expiryTime);
        return Optional.of(authenticationResponseDto);
    }

    public Optional<RegisterRequestDto> register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity user = authenticationMapper.convertRegisterDtoToEntity(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(UserEntity.Role.VIEWER); // 👈 Set mặc định
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return Optional.of(registerRequestDto);
    }
}
