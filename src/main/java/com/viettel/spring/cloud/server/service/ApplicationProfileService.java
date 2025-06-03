package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.dto.applicationprofile.ApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.CreateApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.UpdateApplicationProfileDto;
import com.viettel.spring.cloud.server.entity.ApplicationEntity;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.mapper.ApplicationProfileMapper;
import com.viettel.spring.cloud.server.repository.ApplicationProfileRepository;
import com.viettel.spring.cloud.server.repository.ApplicationRepository;
import com.viettel.spring.cloud.server.util.CriteriaQueryUtil;

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
    private final ApplicationProfileRepository applicationProfileRepository;

    @Autowired
    private final ApplicationRepository applicationRepository;

    @Autowired
    private final ApplicationProfileMapper applicationProfileMapper;

    @Autowired
    private final CriteriaQueryUtil criteriaQueryHelper;

    @Autowired
    private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationProfileService.class);

    public List<ApplicationProfileDto> getAllApplicationProfiles() {
        return applicationProfileRepository.findAll().stream()
                .map(applicationProfileMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<ApplicationProfileDto> getApplicationProfileById(Long id) {
        return applicationProfileRepository.findById(id)
                .map(applicationProfileMapper::convertEntityToDto);
    }

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
