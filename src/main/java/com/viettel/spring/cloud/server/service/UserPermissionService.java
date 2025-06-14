package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.dto.userpermission.UserPermissionDto;
import com.viettel.spring.cloud.server.dto.userpermission.CreateUserPermissionDto;
import com.viettel.spring.cloud.server.dto.userpermission.UpdateUserPermissionDto;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity.Permission;
import com.viettel.spring.cloud.server.mapper.UserPermissionMapper;
import com.viettel.spring.cloud.server.repository.ApplicationProfileRepository;
import com.viettel.spring.cloud.server.repository.UserPermissionRepository;
import com.viettel.spring.cloud.server.repository.UserRepository;
import com.viettel.spring.cloud.server.util.CriteriaQueryUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionService {
    @Autowired
    private final UserPermissionRepository userPermissionRepository;
    

    @Autowired
    private final ApplicationProfileRepository applicationProfileRepository;
    
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPermissionMapper userPermissionMapper;

    @Autowired
    private final CriteriaQueryUtil criteriaQueryHelper;

    @Autowired
    private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(UserPermissionService.class);

    public List<UserPermissionDto> getAllConfigProperties() {
        return userPermissionRepository.findAll().stream()
                .map(userPermissionMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<UserPermissionDto> getUserPermissionById(Long id) {
        return userPermissionRepository.findById(id)
                .map(userPermissionMapper::convertEntityToDto);
    }

    @Transactional
    public CreateUserPermissionDto createUserPermission(CreateUserPermissionDto createUserPermissionDto) {
        ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(createUserPermissionDto.getApplicationProfileId()).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + createUserPermissionDto.getApplicationProfileId()));

        UserEntity userEntity = userRepository.findById(createUserPermissionDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + createUserPermissionDto.getUserId()));

        UserPermissionEntity userPermissionEntity = userPermissionMapper.convertCreateDtoToEntity(createUserPermissionDto);

        userPermissionEntity.setApplicationProfile(applicationProfileEntity);
        userPermissionEntity.setUser(userEntity);
        userPermissionEntity.setCreatedAt(LocalDateTime.now());
        userPermissionEntity.setUpdatedAt(LocalDateTime.now());

        UserPermissionEntity createdUserPermissionEntity = userPermissionRepository.save(userPermissionEntity);
        return userPermissionMapper.convertEntityToCreateDto(createdUserPermissionEntity);
    }

    @Transactional
    public Optional<UpdateUserPermissionDto> updateUserPermission(Long id, UpdateUserPermissionDto updateUserPermissionDto) {
        return userPermissionRepository.findById(id)
                .map(userPermissionEntity -> {
                    if (updateUserPermissionDto.getApplicationProfileId() != null) {
                        ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(updateUserPermissionDto.getApplicationProfileId()).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + updateUserPermissionDto.getApplicationProfileId()));
                        
                        userPermissionEntity.setApplicationProfile(applicationProfileEntity);
                    }

                    if (updateUserPermissionDto.getUserId() != null) {
                        UserEntity UserEntity = userRepository.findById(updateUserPermissionDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + updateUserPermissionDto.getUserId()));
                        
                        userPermissionEntity.setUser(UserEntity);
                    }

                    userPermissionMapper.updateEntityFromDto(updateUserPermissionDto, userPermissionEntity);

                    userPermissionEntity.setUpdatedAt(LocalDateTime.now());

                    UserPermissionEntity updatedUserPermissionEntity = userPermissionRepository.save(userPermissionEntity);

                    return userPermissionMapper.convertEntityToUpdateDto(updatedUserPermissionEntity);
                });
    }

    @Transactional
    public Optional<UserPermissionDto> deleteUserPermission(Long id) {
        return userPermissionRepository.findById(id)
                .map(userPermissionEntity -> {
                    userPermissionRepository.delete(userPermissionEntity);
                    logger.warn("Deleting: " + UserPermissionService.class);
                    return userPermissionMapper.convertEntityToDto(userPermissionEntity);
                });
    }

    public List<UserPermissionDto> findUserPermissionByApplicationProfileId(Long applicationProfileId) {
        List<UserPermissionEntity> entities = criteriaQueryHelper.findByJoinFilters(
            entityManager,
            UserPermissionEntity.class,
            "applicationProfile", // join field
            "id",          // field of ApplicationProfileEntity
            applicationProfileId
        );

        return entities.stream()
            .map(userPermissionMapper::convertEntityToDto)
            .collect(Collectors.toList());
    }

    public List<UserPermissionDto> findUserPermissionByUserId(Long userId) {
        List<UserPermissionEntity> entities = criteriaQueryHelper.findByJoinFilters(
            entityManager,
            UserPermissionEntity.class,
            "user", // join field
            "id",          // field of userEntity
            userId
        );

        return entities.stream()
            .map(userPermissionMapper::convertEntityToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void grantUserPermissions(Long userId, List<Long> applicationProfileIds, List<String> permissionTypes) {
        List<UserPermissionEntity> permissions = new ArrayList<>();
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        for (Long appProfileId : applicationProfileIds) {
            ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(appProfileId).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + appProfileId));

            for (String permissionType : permissionTypes) {
                UserPermissionEntity permission = new UserPermissionEntity();

                permission.setUser(userEntity);
                permission.setApplicationProfile(applicationProfileEntity);
                permission.setPermission(Permission.valueOf(permissionType));
                permission.setCreatedAt(LocalDateTime.now());
                permission.setUpdatedAt(LocalDateTime.now());
                permissions.add(permission);
            }
        }
        
        // ✅ Batch save all permissions at once
        userPermissionRepository.saveAll(permissions);
    }
    
    // ✅ Grant default permissions based on role
    @Transactional
    public void grantDefaultPermissions(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<String> defaultPermissions = getDefaultPermissionsByRole(userEntity.getRole().toString());
        List<Long> allApplicationProfileIds = getAllApplicationProfileIds();
        
        grantUserPermissions(userId, allApplicationProfileIds, defaultPermissions);
    }
    
    private List<String> getDefaultPermissionsByRole(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return Arrays.asList("READ", "WRITE", "DELETE");
            case "leader":
                return Arrays.asList("READ", "WRITE", "DELETE");
            case "developer":
                return Arrays.asList("READ", "WRITE");
            case "viewer":
            default:
                return Arrays.asList("READ");
        }
    }
    
    private List<Long> getAllApplicationProfileIds() {
        return applicationProfileRepository.findAll().stream()
                .map(ApplicationProfileEntity::getId)
                .collect(Collectors.toList());
    }
}
