package com.viettel.spring.cloud.server.security;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;
import com.viettel.spring.cloud.server.repository.UserPermissionRepository;
import com.viettel.spring.cloud.server.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, Serializable {
    private static final long serialVersionUID = 1L;
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPermissionRepository userPermissionRepository;
    
    @Cacheable(value = "userDetails", key = "#username")
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user from database: {}", username); // Debug cache miss
        UserEntity userEntity = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<UserPermissionEntity> permissions = userPermissionRepository.findByUser(userEntity);
        return CustomUserDetails.fromUserEntity(userEntity, permissions);
    }
    
    @CacheEvict(value = "userDetails", key = "#username") 
    public void evictUserFromCache(String username) {
        log.info("Evicting user from cache: {}", username);
    }
}
