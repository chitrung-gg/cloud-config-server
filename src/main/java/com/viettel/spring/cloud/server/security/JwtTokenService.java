package com.viettel.spring.cloud.server.security;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${JWT_ISSUER}")
    private String issuer;

    @Value("${JWT_EXPIRY_TIME}")
    private Long expiryTime;

    // @Autowired
    // private final AuthenticationManager authenticationManager;

    // private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        
        List<String> authorities = 
        Optional.ofNullable
        (customUserDetails.getAuthorities())
        .orElse(Collections.emptyList())
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

        return Jwts.builder()
                .issuer(issuer)
                .subject(customUserDetails.getUsername())
                .claim("userId", customUserDetails.getId())
                .claim("role", customUserDetails.getRole().name())
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryTime * 1000))
                .signWith(secretKey, Jwts.SIG.HS256).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    private boolean isTokenExpired(String token) {
        // For debugging issue, can return immediately
        Date expirationDate = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}