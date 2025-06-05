package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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

    // @Async
    // @Retry(name = "customSetting")
    // @CircuitBreaker(name = "customSetting")    
    public List<ApplicationProfileDto> getAllApplicationProfiles(CustomUserDetails userDetails) {

        Set<Long> accessibleProfileIds = getAccessibleProfileIds(userDetails, Permission.READ, Permission.WRITE);
            
        return applicationProfileRepository.findAllById(accessibleProfileIds).stream()
        .map(applicationProfileMapper::convertEntityToDto)
        .collect(Collectors.toList());
    }
        
    public Optional<ApplicationProfileDto> getApplicationProfileById(Long id, CustomUserDetails userDetails) {
        Set<Long> accessibleProfileIds = getAccessibleProfileIds(userDetails, Permission.READ, Permission.WRITE);

        if (!accessibleProfileIds.contains(id)) {
            return Optional.empty();
        }
        return applicationProfileRepository.findById(id)
            .map(applicationProfileMapper::convertEntityToDto);
    }

    @Async
    @Retry(name = "customSetting")
    @CircuitBreaker(name = "customSetting")
    public List<ApplicationProfileDto> getApplicationProfileByLabel(String label) {
        return applicationProfileRepository.findByLabel(label).stream()
                .map(applicationProfileMapper::convertEntityToDto).collect(Collectors.toList());
    }

    public List<ApplicationProfileDto> getApplicationProfileByProfile(String profile) {
        return applicationProfileRepository.findByProfile(profile).stream()
                .map(applicationProfileMapper::convertEntityToDto).collect(Collectors.toList());
    }

    @Transactional
    public CreateApplicationProfileDto createApplicationProfile(CreateApplicationProfileDto createApplicationProfileDto) {
        ApplicationEntity applicationEntity = applicationRepository.findById(createApplicationProfileDto.getApplicationId()).orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + createApplicationProfileDto.getApplicationId()));

        ApplicationProfileEntity applicationProfileEntity = applicationProfileMapper.convertCreateDtoToEntity(createApplicationProfileDto);

        applicationProfileEntity.setApplication(applicationEntity);
        applicationProfileEntity.setCreatedAt(LocalDateTime.now());
        applicationProfileEntity.setUpdatedAt(LocalDateTime.now());

        ApplicationProfileEntity createdApplicationProfileEntity = applicationProfileRepository.save(applicationProfileEntity);

        Long userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Grant full permissions
        for (UserPermissionEntity.Permission permission : List.of(UserPermissionEntity.Permission.READ, UserPermissionEntity.Permission.WRITE, UserPermissionEntity.Permission.DELETE)) {
            UserPermissionEntity userPermission = new UserPermissionEntity();
            userPermission.setUser(userEntity);
            userPermission.setApplicationProfile(createdApplicationProfileEntity);
            userPermission.setPermission(permission);
            userPermission.setCreatedAt(LocalDateTime.now());
            userPermission.setUpdatedAt(LocalDateTime.now());
            userPermissionRepository.save(userPermission);
        }
        
        return applicationProfileMapper.convertEntityToCreateDto(createdApplicationProfileEntity);
    }

    @Transactional
    public Optional<UpdateApplicationProfileDto> updateApplicationProfile(Long id, UpdateApplicationProfileDto updateApplicationProfileDto) {
        return applicationProfileRepository.findById(id)
                .map(applicationProfileEntity -> {
                    if (updateApplicationProfileDto.getApplicationId() != null) {
                        ApplicationEntity applicationEntity = applicationRepository.findById(updateApplicationProfileDto.getApplicationId()).orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + updateApplicationProfileDto.getApplicationId()));
                        
                        applicationProfileEntity.setApplication(applicationEntity);
                    }

                    applicationProfileMapper.updateEntityFromDto(updateApplicationProfileDto, applicationProfileEntity);

                    applicationProfileEntity.setUpdatedAt(LocalDateTime.now());

                    ApplicationProfileEntity updatedApplicationProfileEntity = applicationProfileRepository.save(applicationProfileEntity);

                    return applicationProfileMapper.convertEntityToUpdateDto(updatedApplicationProfileEntity);
                });
    }

    @Transactional
    public Optional<ApplicationProfileDto> deleteApplicationProfile(Long id) {
        return applicationProfileRepository.findById(id)
                .map(applicationProfileEntity -> {
                    applicationProfileRepository.delete(applicationProfileEntity);
                    logger.warn("Deleting: " + ApplicationProfileService.class);
                    return applicationProfileMapper.convertEntityToDto(applicationProfileEntity);
                });
    }

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
