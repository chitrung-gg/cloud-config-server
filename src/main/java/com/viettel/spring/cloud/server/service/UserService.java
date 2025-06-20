package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.viettel.spring.cloud.server.dto.user.UserDto;
import com.viettel.spring.cloud.server.dto.user.CreateUserDto;
import com.viettel.spring.cloud.server.dto.user.UpdateUserDto;
import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.mapper.UserMapper;
import com.viettel.spring.cloud.server.repository.UserRepository;

// import com.viettel.spring.cloud.server.helper.CriteriaQueryHelper;
// import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    // @Autowired
    // private final CriteriaQueryHelper criteriaQueryHelper;

    // @Autowired
    // private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private boolean isPasswordHashed(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // BCrypt hash pattern: starts with $2a$, $2b$, $2x$, $2y$ và có độ dài 60 ký tự
        return password.matches("^\\$2[abxy]\\$\\d{2}\\$.{53}$") && password.length() == 60;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::convertEntityToDto);
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::convertEntityToDto);
    }

    public List<UserDto> getUserByRole(UserEntity.Role Role) {
        return userRepository.findByRole(Role).stream()
                .map(userMapper::convertEntityToDto).collect(Collectors.toList());
    }

    @Transactional
    public CreateUserDto createUser(CreateUserDto createUserDto) {
        UserEntity userEntity = userMapper.convertCreateDtoToEntity(createUserDto);

        // Hash password if provided and not already hashed
        if (StringUtils.hasText(createUserDto.getPassword()) && !isPasswordHashed(createUserDto.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        }
        
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        UserEntity createdUserEntity = userRepository.save(userEntity);
        return userMapper.convertEntityToCreateDto(createdUserEntity);
    }

    @Transactional
    public Optional<UpdateUserDto> updateUser(Long id, UpdateUserDto updateUserDto) {
        return userRepository.findById(id)
                .map(userEntity -> {

                    userMapper.updateEntityFromDto(updateUserDto, userEntity);

                    if (StringUtils.hasText(updateUserDto.getPassword()) && !isPasswordHashed(updateUserDto.getPassword())) {
                        userEntity.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
                    }

                    userEntity.setUpdatedAt(LocalDateTime.now());

                    UserEntity updatedUserEntity = userRepository.save(userEntity);

                    return userMapper.convertEntityToUpdateDto(updatedUserEntity);
                });
    }

    @Transactional
    public Optional<UserDto> deleteUser(Long id) {
        return userRepository.findById(id)
                .map(UserEntity -> {
                    userRepository.delete(UserEntity);
                    logger.warn("Deleting: " + UserService.class);
                    return userMapper.convertEntityToDto(UserEntity);
                });
    }

    // public List<UserDto> findUserByApplicationProfileId(Long applicationProfileId) {
    //     List<UserEntity> entities = criteriaQueryHelper.findByJoinFilters(
    //         entityManager,
    //         UserEntity.class,
    //         "applicationProfile", // join field
    //         "id",          // field of ApplicationProfileEntity
    //         applicationProfileId
    //     );

    //     return entities.stream()
    //         .map(UserMapper::convertEntityToDto)
    //         .collect(Collectors.toList());
    // }
}