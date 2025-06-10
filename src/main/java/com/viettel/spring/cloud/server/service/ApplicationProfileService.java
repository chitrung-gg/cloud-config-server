package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.dto.applicationprofile.ApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.CreateApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.UpdateApplicationProfileDto;
import com.viettel.spring.cloud.server.entity.ApplicationEntity;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity.Permission;
import com.viettel.spring.cloud.server.mapper.ApplicationProfileMapper;
import com.viettel.spring.cloud.server.mapper.CustomUserDetailsMapper;
import com.viettel.spring.cloud.server.repository.ApplicationProfileRepository;
import com.viettel.spring.cloud.server.repository.ApplicationRepository;
import com.viettel.spring.cloud.server.repository.UserPermissionRepository;
import com.viettel.spring.cloud.server.repository.UserRepository;
import com.viettel.spring.cloud.server.security.CustomUserDetails;
import com.viettel.spring.cloud.server.util.CriteriaQueryUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationProfileService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPermissionRepository userPermissionRepository;

    @Autowired
    private final ApplicationProfileRepository applicationProfileRepository;

    @Autowired
    private final ApplicationRepository applicationRepository;

    @Autowired
    private final ApplicationProfileMapper applicationProfileMapper;

    @Autowired
    private final CustomUserDetailsMapper userDetailsMapper;

    @Autowired
    private final CriteriaQueryUtil criteriaQueryHelper;

    @Autowired
    private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationProfileService.class);

    private Set<Long> getAccessibleProfileIds(CustomUserDetails userDetails, UserPermissionEntity.Permission... permissions) {
        UserEntity user = userDetailsMapper.convertCustomDetailsToEntity(userDetails);

        Set<Permission> allowedPermissions = Set.of(permissions);

        return userPermissionRepository.findByUser(user).stream()
            .filter(p -> allowedPermissions.contains(p.getPermission()))
            .map(p -> p.getApplicationProfile().getId())
            .collect(Collectors.toSet());
    }

    private boolean hasPermission(CustomUserDetails userDetails, Long profileId, UserPermissionEntity.Permission requiredPermission) {
        // Admin has all permissions
        if (userDetails.getRole() == UserEntity.Role.ADMIN) {
            return true;
        }
        
        Set<Long> accessibleProfileIds = getAccessibleProfileIds(userDetails, requiredPermission);
        return accessibleProfileIds.contains(profileId);
    }

    private void checkRolePermission(CustomUserDetails userDetails, String operation) {
        UserEntity.Role role = userDetails.getRole();
        
        switch (operation.toLowerCase()) {
            case "read":
                // All roles can read (if they have access to the profile)
                break;
            case "write":
            case "update":
                if (role == UserEntity.Role.VIEWER) {
                    throw new AccessDeniedException("Viewers cannot perform write operations");
                }
                break;
            case "delete":
                if (role == UserEntity.Role.VIEWER || role == UserEntity.Role.DEVELOPER) {
                    throw new AccessDeniedException("Only ADMIN and LEADER roles can delete profiles");
                }
                break;
            default:
                throw new AccessDeniedException("Unknown operation: " + operation);
        }
    }

    @Retry(name = "customSetting")
    @CircuitBreaker(name = "customSetting") 
    @Cacheable(value = "applicationProfile", key = "T(String).format('%s:%s', #root.method.name, #userDetails.username)", condition = "#result != null and #result.size() > 0")   
    public List<ApplicationProfileDto> getAllApplicationProfiles(CustomUserDetails userDetails) {
        // try {
        //     Thread.sleep(1000); // Delay 200ms for testing Elapsed Time
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        // }
        // throw new RuntimeException("Simulated failure");
        checkRolePermission(userDetails, "read");
        
        if (userDetails.getRole() == UserEntity.Role.ADMIN) {
            // Admin can see all profiles
            logger.info("Admin user {} accessing all application profiles", userDetails.getUsername());
            return applicationProfileRepository.findAll().stream()
                    .map(applicationProfileMapper::convertEntityToDto)
                    .collect(Collectors.toList());
        } else {
            // Other roles can only see profiles they have READ access to
            Set<Long> accessibleProfileIds = getAccessibleProfileIds(userDetails, Permission.READ);
            
            if (accessibleProfileIds.isEmpty()) {
                logger.warn("User {} has no accessible profiles", userDetails.getUsername());
                return Collections.emptyList();
            }
            
            logger.info("User {} with role {} accessing {} accessible profiles", 
                       userDetails.getUsername(), userDetails.getRole(), accessibleProfileIds.size());
            
            return applicationProfileRepository.findAllById(accessibleProfileIds).stream()
                    .map(applicationProfileMapper::convertEntityToDto)
                    .collect(Collectors.toList());
        }
    }
        
    @Cacheable(value = "applicationProfile", key = "T(String).format('%s:%s:%s', #root.method.name, #id, #userDetails.username)")
    public Optional<ApplicationProfileDto> getApplicationProfileById(Long id, CustomUserDetails userDetails) {
        checkRolePermission(userDetails, "read");
        
        // Admin can access any profile
        if (userDetails.getRole() == UserEntity.Role.ADMIN) {
            return applicationProfileRepository.findById(id)
                .map(applicationProfileMapper::convertEntityToDto);
        }
        
        // Other roles need READ permission
        if (!hasPermission(userDetails, id, Permission.READ)) {
            logger.warn("User {} with role {} denied access to profile {}", 
                       userDetails.getUsername(), userDetails.getRole(), id);
            return Optional.empty();
        }
        
        return applicationProfileRepository.findById(id)
            .map(applicationProfileMapper::convertEntityToDto);
    }

    @Retry(name = "customSetting")
    @CircuitBreaker(name = "customSetting")
    @Cacheable(value = "applicationProfile", key = "T(String).format('%s:%s', #root.method.name, #label)")
    public List<ApplicationProfileDto> getApplicationProfileByLabel(String label) {
        // This method should also be secured - for now keeping original logic
        return applicationProfileRepository.findByLabel(label).stream()
                .map(applicationProfileMapper::convertEntityToDto).collect(Collectors.toList());
    }

    @Cacheable(value = "applicationProfile", key = "T(String).format('%s:%s', #root.method.name, #profile)")
    public List<ApplicationProfileDto> getApplicationProfileByProfile(String profile) {
        // This method should also be secured - for now keeping original logic
        return applicationProfileRepository.findByProfile(profile).stream()
                .map(applicationProfileMapper::convertEntityToDto).collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "applicationProfile", allEntries = true)
    })
    public CreateApplicationProfileDto createApplicationProfile(CreateApplicationProfileDto createApplicationProfileDto) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkRolePermission(userDetails, "write");
        
        ApplicationEntity applicationEntity = applicationRepository.findById(createApplicationProfileDto.getApplicationId())
            .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + createApplicationProfileDto.getApplicationId()));

        ApplicationProfileEntity applicationProfileEntity = applicationProfileMapper.convertCreateDtoToEntity(createApplicationProfileDto);

        applicationProfileEntity.setApplication(applicationEntity);
        applicationProfileEntity.setCreatedAt(LocalDateTime.now());
        applicationProfileEntity.setUpdatedAt(LocalDateTime.now());

        ApplicationProfileEntity createdApplicationProfileEntity = applicationProfileRepository.save(applicationProfileEntity);

        Long userId = userDetails.getId();
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Grant full permissions to creator
        for (UserPermissionEntity.Permission permission : List.of(Permission.READ, Permission.WRITE, Permission.DELETE)) {
            UserPermissionEntity userPermission = new UserPermissionEntity();
            userPermission.setUser(userEntity);
            userPermission.setApplicationProfile(createdApplicationProfileEntity);
            userPermission.setPermission(permission);
            userPermission.setCreatedAt(LocalDateTime.now());
            userPermission.setUpdatedAt(LocalDateTime.now());
            userPermissionRepository.save(userPermission);
        }
        
        logger.info("User {} with role {} created application profile {}", 
                   userDetails.getUsername(), userDetails.getRole(), createdApplicationProfileEntity.getId());
        
        return applicationProfileMapper.convertEntityToCreateDto(createdApplicationProfileEntity);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "applicationProfile", allEntries = true)
    })
    public Optional<UpdateApplicationProfileDto> updateApplicationProfile(Long id, UpdateApplicationProfileDto updateApplicationProfileDto) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkRolePermission(userDetails, "write");
        
        // Check if user has WRITE permission for this profile
        if (!hasPermission(userDetails, id, Permission.WRITE)) {
            logger.warn("User {} with role {} denied update access to profile {}", 
                       userDetails.getUsername(), userDetails.getRole(), id);
            throw new AccessDeniedException("You don't have permission to update this application profile");
        }
        
        return applicationProfileRepository.findById(id)
                .map(applicationProfileEntity -> {
                    if (updateApplicationProfileDto.getApplicationId() != null) {
                        ApplicationEntity applicationEntity = applicationRepository.findById(updateApplicationProfileDto.getApplicationId())
                            .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + updateApplicationProfileDto.getApplicationId()));
                        
                        applicationProfileEntity.setApplication(applicationEntity);
                    }

                    applicationProfileMapper.updateEntityFromDto(updateApplicationProfileDto, applicationProfileEntity);
                    applicationProfileEntity.setUpdatedAt(LocalDateTime.now());

                    ApplicationProfileEntity updatedApplicationProfileEntity = applicationProfileRepository.save(applicationProfileEntity);
                    
                    logger.info("User {} with role {} updated application profile {}", 
                               userDetails.getUsername(), userDetails.getRole(), id);

                    return applicationProfileMapper.convertEntityToUpdateDto(updatedApplicationProfileEntity);
                });
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "applicationProfile", allEntries = true)
    })
    public Optional<ApplicationProfileDto> deleteApplicationProfile(Long id) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkRolePermission(userDetails, "delete");
        
        // Check if user has DELETE permission for this profile (ADMIN or LEADER with access)
        if (!hasPermission(userDetails, id, Permission.DELETE)) {
            logger.warn("User {} with role {} denied delete access to profile {}", 
                       userDetails.getUsername(), userDetails.getRole(), id);
            throw new AccessDeniedException("You don't have permission to delete this application profile");
        }
        
        return applicationProfileRepository.findById(id)
                .map(applicationProfileEntity -> {
                    ApplicationProfileDto dto = applicationProfileMapper.convertEntityToDto(applicationProfileEntity);
                    applicationProfileRepository.delete(applicationProfileEntity);
                    
                    logger.warn("User {} with role {} deleted application profile {}", 
                               userDetails.getUsername(), userDetails.getRole(), id);
                    
                    return dto;
                });
    }

    @Cacheable(value = "applicationProfile", key = "T(String).format('%s:%s', #root.method.name, #applicationId)")
    public List<ApplicationProfileDto> findProfilesByApplicationId(Long applicationId) {
        List<ApplicationProfileEntity> entities = criteriaQueryHelper.findByJoinFilters(
            entityManager,
            ApplicationProfileEntity.class,
            "application", // join field
            "id",          // field of ApplicationEntity
            applicationId
        );

        return entities.stream()
            .map(applicationProfileMapper::convertEntityToDto)
            .collect(Collectors.toList());
    }
}
