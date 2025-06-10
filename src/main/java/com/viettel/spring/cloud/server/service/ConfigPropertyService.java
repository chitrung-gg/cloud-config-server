package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.endpoint.RefreshBusEndpoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.viettel.spring.cloud.server.dto.configproperty.ConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.CreateConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.UpdateConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configversion.CreateConfigVersionDto;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;
import com.viettel.spring.cloud.server.mapper.ConfigPropertyMapper;
import com.viettel.spring.cloud.server.repository.ApplicationProfileRepository;
import com.viettel.spring.cloud.server.repository.ConfigPropertyRepository;
import com.viettel.spring.cloud.server.util.CriteriaQueryUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigPropertyService {
    @Autowired
    private final ConfigPropertyRepository configPropertyRepository;

    @Autowired
    private final ApplicationProfileRepository applicationProfileRepository;

    @Autowired
    private final ConfigPropertyMapper configPropertyMapper;

    @Autowired
    private final CriteriaQueryUtil criteriaQueryHelper;

    @Autowired
    private final EntityManager entityManager;

    @Autowired
    private final ConfigVersionService configVersionService;

    @Autowired
    private final RefreshBusEndpoint refreshBusEndpoint;

    private static final Logger logger = LoggerFactory.getLogger(ConfigPropertyService.class);

    public List<ConfigPropertyDto> getAllConfigProperties() {
        return configPropertyRepository.findAll().stream()
                .map(configPropertyMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<ConfigPropertyDto> getConfigPropertyById(Long id) {
        return configPropertyRepository.findById(id)
                .map(configPropertyMapper::convertEntityToDto);
    }

    public Optional<ConfigPropertyDto> getConfigPropertyByKey(String key) {
        return configPropertyRepository.findByKey(key)
                .map(configPropertyMapper::convertEntityToDto);
    }

    public List<ConfigPropertyDto> getConfigPropertyByValue(String value) {
        return configPropertyRepository.findByValue(value).stream()
                .map(configPropertyMapper::convertEntityToDto).collect(Collectors.toList());
    }

    public List<ConfigPropertyDto> getConfigPropertyByFormat(String format) {
        return configPropertyRepository.findByFormat(format).stream()
                .map(configPropertyMapper::convertEntityToDto).collect(Collectors.toList());
    }

    @Transactional
    public CreateConfigPropertyDto createConfigProperty(CreateConfigPropertyDto createConfigPropertyDto) {
        ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(createConfigPropertyDto.getApplicationProfileId()).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + createConfigPropertyDto.getApplicationProfileId()));

        ConfigPropertyEntity configPropertyEntity = configPropertyMapper.convertCreateDtoToEntity(createConfigPropertyDto);

        configPropertyEntity.setApplicationProfile(applicationProfileEntity);
        configPropertyEntity.setCreatedAt(LocalDateTime.now());
        configPropertyEntity.setUpdatedAt(LocalDateTime.now());

        ConfigPropertyEntity createdConfigPropertyEntity = configPropertyRepository.save(configPropertyEntity);

        configVersionService.saveSnapshot(new CreateConfigVersionDto(
            createdConfigPropertyEntity.getApplicationProfile().getId(),
            "Time: " + createdConfigPropertyEntity.getApplicationProfile().getUpdatedAt())
        );
        
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Async
            @Override
            public void afterCommit() {
                CompletableFuture.runAsync(() -> {
                    try {
                        // Thread.sleep(100); // 100ms delay
                        refreshBusEndpoint.busRefreshWithDestination(new String[]{"**"});
                        log.info("Bus refresh sent successfully");
                    } catch (Exception e) {
                        log.error("Failed to send bus refresh", e);
                    }
                });
            }
        });
        return configPropertyMapper.convertEntityToCreateDto(createdConfigPropertyEntity);
    }

    @Transactional
    public Optional<UpdateConfigPropertyDto> updateConfigProperty(Long id, UpdateConfigPropertyDto updateConfigPropertyDto) {
        return configPropertyRepository.findById(id)
                .map(configPropertyEntity -> {
                    if (updateConfigPropertyDto.getApplicationProfileId() != null) {
                        ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(updateConfigPropertyDto.getApplicationProfileId()).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + updateConfigPropertyDto.getApplicationProfileId()));
                        
                        
                        configPropertyEntity.setApplicationProfile(applicationProfileEntity);
                    }

                    
                    configPropertyMapper.updateEntityFromDto(updateConfigPropertyDto, configPropertyEntity);
                    
                    configPropertyEntity.setUpdatedAt(LocalDateTime.now());
                    
                    ConfigPropertyEntity updatedConfigPropertyEntity = configPropertyRepository.save(configPropertyEntity);

                    configVersionService.saveSnapshot(new CreateConfigVersionDto(
                        updatedConfigPropertyEntity.getApplicationProfile().getId(), "Time: " + updatedConfigPropertyEntity.getApplicationProfile().getUpdatedAt())
                    );

                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Async
                        @Override
                        public void afterCommit() {
                            CompletableFuture.runAsync(() -> {
                                try {
                                    // Thread.sleep(100); // 100ms delay
                                    refreshBusEndpoint.busRefreshWithDestination(new String[]{"**"});
                                    log.info("Bus refresh sent successfully");
                                } catch (Exception e) {
                                    log.error("Failed to send bus refresh", e);
                                }
                            });
                        }
                    });

                    return configPropertyMapper.convertEntityToUpdateDto(updatedConfigPropertyEntity);
                });
    }

    @Transactional
    public Optional<ConfigPropertyDto> deleteConfigProperty(Long id) {
        return configPropertyRepository.findById(id)
                .map(ConfigPropertyEntity -> {
                    configPropertyRepository.delete(ConfigPropertyEntity);
                    logger.warn("Deleting: " + ConfigPropertyService.class);
                    return configPropertyMapper.convertEntityToDto(ConfigPropertyEntity);
                });
    }

    public List<ConfigPropertyDto> findConfigPropertyByApplicationProfileId(Long applicationProfileId) {
        List<ConfigPropertyEntity> entities = criteriaQueryHelper.findByJoinFilters(
            entityManager,
            ConfigPropertyEntity.class,
            "applicationProfile", // join field
            "id",          // field of ApplicationProfileEntity
            applicationProfileId
        );

        return entities.stream()
            .map(configPropertyMapper::convertEntityToDto)
            .collect(Collectors.toList());
    }
    
}