package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.spring.cloud.server.dto.application.ApplicationDto;
import com.viettel.spring.cloud.server.dto.application.CreateApplicationDto;
import com.viettel.spring.cloud.server.dto.application.UpdateApplicationDto;
import com.viettel.spring.cloud.server.entity.ApplicationEntity;
import com.viettel.spring.cloud.server.entity.ApplicationMetadataEntity;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.mapper.ApplicationMapper;
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
public class ApplicationService {
    @Autowired
    private final ApplicationRepository applicationRepository;

    @Autowired
    private final ApplicationMapper applicationMapper;

    @Autowired
    private final ApplicationMetadataRepository applicationMetadataRepository;

    @Autowired
    private final ApplicationMetadataMapper applicationMetadataMapper;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public List<ApplicationDto> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(applicationMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<ApplicationDto> getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(applicationMapper::convertEntityToDto);
    }

    public Optional<ApplicationDto> getApplicationByName(String name) {
        return applicationRepository.findByName(name)
                .map(applicationMapper::convertEntityToDto);
    }

    @Transactional
    public CreateApplicationDto createApplication(CreateApplicationDto createApplicationDto) {
        ApplicationEntity applicationEntity = applicationMapper.convertCreateDtoToEntity(createApplicationDto);
        applicationEntity.setCreatedAt(LocalDateTime.now());
        applicationEntity.setUpdatedAt(LocalDateTime.now());

        ApplicationEntity savedApplicationEntity = applicationRepository.save(applicationEntity);
        
        if (createApplicationDto.getMetadata() != null) {
            ApplicationMetadataEntity applicationMetadataEntity = applicationMetadataMapper.convertDtoToEntity(createApplicationDto.getMetadata());
            applicationMetadataEntity.setApplication(savedApplicationEntity); // Set reference
            applicationMetadataEntity.setCreatedAt(LocalDateTime.now());
            applicationMetadataEntity.setUpdatedAt(LocalDateTime.now());
            
            ApplicationMetadataEntity savedMetadata = applicationMetadataRepository.save(applicationMetadataEntity);
            savedApplicationEntity.setMetadata(savedMetadata);
        }
        ApplicationEntity createdApplicationEntity = applicationRepository.save(applicationEntity);
        return applicationMapper.convertEntityToCreateDto(createdApplicationEntity);
    }

    @Transactional
    public Optional<UpdateApplicationDto> updateApplication(Long id, UpdateApplicationDto updateApplicationDto) {
        return applicationRepository.findById(id)
                .map(applicationEntity -> {
                    // Cập nhật thông tin application
                    applicationMapper.updateEntityFromDto(updateApplicationDto, applicationEntity);
                    applicationEntity.setUpdatedAt(LocalDateTime.now());

                    // Xử lý metadata
                    if (updateApplicationDto.getMetadata() != null) {
                        if (applicationEntity.getMetadata() != null) {
                            // Cập nhật metadata hiện có
                            applicationMetadataMapper.updateEntityFromDto(updateApplicationDto.getMetadata(), applicationEntity.getMetadata());
                            applicationEntity.getMetadata().setUpdatedAt(LocalDateTime.now());
                        } else {
                            // Tạo mới metadata nếu chưa có - sử dụng convertUpdateDtoToEntity
                            ApplicationMetadataEntity newMetadata = applicationMetadataMapper.convertDtoToEntity(updateApplicationDto.getMetadata());

                            newMetadata.setApplication(applicationEntity);
                            newMetadata.setCreatedAt(LocalDateTime.now());
                            newMetadata.setUpdatedAt(LocalDateTime.now());
                            
                            ApplicationMetadataEntity savedMetadata = applicationMetadataRepository.save(newMetadata);

                            applicationEntity.setMetadata(savedMetadata);
                        }
                    }

                    ApplicationEntity updatedApplicationEntity = applicationRepository.save(applicationEntity);
                    return applicationMapper.convertEntityToUpdateDto(updatedApplicationEntity);
                });
    }

    @Transactional
    public Optional<ApplicationDto> deleteApplication(Long id) {
        return applicationRepository.findById(id)
                .map(applicationEntity -> {
                    applicationRepository.delete(applicationEntity);
                    logger.warn("Deleting: " + ApplicationService.class);
                    return applicationMapper.convertEntityToDto(applicationEntity);
                });
    }
}
