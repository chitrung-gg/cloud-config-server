package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.dto.applicationmetadata.CreateApplicationMetadataDto;
import com.viettel.spring.cloud.server.dto.applicationmetadata.UpdateApplicationMetadataDto;
import com.viettel.spring.cloud.server.dto.applicationmetadata.ApplicationMetadataDto;
import com.viettel.spring.cloud.server.entity.ApplicationEntity;
import com.viettel.spring.cloud.server.entity.ApplicationMetadataEntity;
import com.viettel.spring.cloud.server.mapper.ApplicationMetadataMapper;
import com.viettel.spring.cloud.server.repository.ApplicationMetadataRepository;
import com.viettel.spring.cloud.server.repository.ApplicationRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationMetadataService {
    
    @Autowired
    private final ApplicationRepository applicationRepository;
    @Autowired
    private final ApplicationMetadataRepository applicationMetadataRepository;

    @Autowired
    private final ApplicationMetadataMapper applicationMetadataMapper;

    // @Autowired
    // private final CriteriaQueryHelper criteriaQueryHelper;

    // @Autowired
    // private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMetadataService.class);

    public List<ApplicationMetadataDto> getAllApplicationMetadatas() {
        return applicationMetadataRepository.findAll().stream()
                .map(applicationMetadataMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<ApplicationMetadataDto> getApplicationMetadataById(Long id) {
        return applicationMetadataRepository.findById(id)
                .map(applicationMetadataMapper::convertEntityToDto);
    }

    @Transactional
    public CreateApplicationMetadataDto createApplicationMetadata(CreateApplicationMetadataDto createApplicationMetadataDto) {
        ApplicationEntity applicationEntity = applicationRepository.findById(createApplicationMetadataDto.getApplicationId())
            .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + createApplicationMetadataDto.getApplicationId()));

        ApplicationMetadataEntity applicationMetadataEntity = applicationMetadataMapper.convertCreateDtoToEntity(createApplicationMetadataDto);

        applicationMetadataEntity.setApplication(applicationEntity);
        applicationMetadataEntity.setCreatedAt(LocalDateTime.now());
        applicationMetadataEntity.setUpdatedAt(LocalDateTime.now());

        ApplicationMetadataEntity createdApplicationMetadataEntity = applicationMetadataRepository.save(applicationMetadataEntity);

        return applicationMetadataMapper.convertEntityToCreateDto(createdApplicationMetadataEntity);
    }

    @Transactional
    public Optional<UpdateApplicationMetadataDto> updateApplicationMetadata(Long id, UpdateApplicationMetadataDto updateApplicationMetadataDto) {
        return applicationMetadataRepository.findById(id)
                .map(applicationMetadataEntity -> {

                    if (updateApplicationMetadataDto.getApplicationId() != null) {
                        ApplicationEntity applicationEntity = applicationRepository.findById(updateApplicationMetadataDto.getApplicationId())
                            .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + updateApplicationMetadataDto.getApplicationId()));
                        
                        applicationMetadataEntity.setApplication(applicationEntity);
                    }
                    applicationMetadataMapper.updateEntityFromUpdateDto(updateApplicationMetadataDto, applicationMetadataEntity);

                    applicationMetadataEntity.setUpdatedAt(LocalDateTime.now());

                    ApplicationMetadataEntity updatedApplicationMetadataEntity = applicationMetadataRepository.save(applicationMetadataEntity);

                    return applicationMetadataMapper.convertEntityToUpdateDto(updatedApplicationMetadataEntity);
                });
    }

    @Transactional
    public Optional<ApplicationMetadataDto> deleteApplicationMetadata(Long id) {
        return applicationMetadataRepository.findById(id)
                .map(applicationMetadataEntity -> {
                    applicationMetadataRepository.delete(applicationMetadataEntity);
                    logger.warn("Deleting: " + ApplicationMetadataService.class);
                    return applicationMetadataMapper.convertEntityToDto(applicationMetadataEntity);
                });
    }
}
