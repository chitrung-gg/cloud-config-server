package com.viettel.spring.cloud.server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.spring.cloud.server.dto.configproperty.ConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.ConfigPropertySnapshotDto;
import com.viettel.spring.cloud.server.dto.configversion.ConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.CreateConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.UpdateConfigVersionDto;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;
import com.viettel.spring.cloud.server.entity.ConfigVersionEntity;
import com.viettel.spring.cloud.server.mapper.ConfigPropertyMapper;
import com.viettel.spring.cloud.server.mapper.ConfigVersionMapper;
import com.viettel.spring.cloud.server.repository.ApplicationProfileRepository;
import com.viettel.spring.cloud.server.repository.ConfigPropertyRepository;
import com.viettel.spring.cloud.server.repository.ConfigVersionRepository;
import com.viettel.spring.cloud.server.util.CriteriaQueryUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigVersionService {
    @Autowired
    private final ConfigVersionRepository configVersionRepository;

    @Autowired
    private final ConfigPropertyRepository configPropertyRepository;

    @Autowired
    private final ApplicationProfileRepository applicationProfileRepository;
    
    @Autowired
    private final ConfigVersionMapper configVersionMapper;

    @Autowired
    private final ConfigPropertyMapper configPropertyMapper;

    @Autowired
    private final CriteriaQueryUtil criteriaQueryHelper;

    @Autowired
    private final EntityManager entityManager;

    @Autowired
    private final RefreshBusEndpoint refreshBusEndpoint;

    @Autowired
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(ConfigVersionService.class);

    public List<ConfigVersionDto> getAllConfigProperties() {
        return configVersionRepository.findAll().stream()
                .map(configVersionMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<ConfigVersionDto> getConfigVersionById(Long id) {
        return configVersionRepository.findById(id)
                .map(configVersionMapper::convertEntityToDto);
    }

    @Transactional
    public CreateConfigVersionDto createConfigVersion(CreateConfigVersionDto createConfigVersionDto) {
        ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(createConfigVersionDto.getApplicationProfileId()).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + createConfigVersionDto.getApplicationProfileId()));

        ConfigVersionEntity configVersionEntity = configVersionMapper.convertCreateDtoToEntity(createConfigVersionDto);

        configVersionEntity.setApplicationProfile(applicationProfileEntity);
        configVersionEntity.setCreatedAt(LocalDateTime.now());
        configVersionEntity.setUpdatedAt(LocalDateTime.now());

        ConfigVersionEntity createdConfigVersionEntity = configVersionRepository.save(configVersionEntity);
        return configVersionMapper.convertEntityToCreateDto(createdConfigVersionEntity);
    }

    @Transactional
    public Optional<UpdateConfigVersionDto> updateConfigVersion(Long id, UpdateConfigVersionDto updateConfigVersionDto) {
        return configVersionRepository.findById(id)
                .map(configVersionEntity -> {
                    if (updateConfigVersionDto.getApplicationProfileId() != null) {
                        ApplicationProfileEntity applicationProfileEntity = applicationProfileRepository.findById(updateConfigVersionDto.getApplicationProfileId()).orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + updateConfigVersionDto.getApplicationProfileId()));
                        
                        configVersionEntity.setApplicationProfile(applicationProfileEntity);
                    }

                    configVersionMapper.updateEntityFromDto(updateConfigVersionDto, configVersionEntity);

                    configVersionEntity.setUpdatedAt(LocalDateTime.now());

                    ConfigVersionEntity updatedConfigVersionEntity = configVersionRepository.save(configVersionEntity);

                    return configVersionMapper.convertEntityToUpdateDto(updatedConfigVersionEntity);
                });
    }

    @Transactional
    public Optional<ConfigVersionDto> deleteConfigVersion(Long id) {
        return configVersionRepository.findById(id)
                .map(configVersionEntity -> {
                    configVersionRepository.delete(configVersionEntity);
                    logger.warn("Deleting: " + ConfigVersionService.class);
                    return configVersionMapper.convertEntityToDto(configVersionEntity);
                });
    }

    public List<ConfigVersionDto> findConfigVersionByApplicationProfileId(Long applicationProfileId) {
        List<ConfigVersionEntity> entities = criteriaQueryHelper.findByJoinFilters(
            entityManager,
            ConfigVersionEntity.class,
            "applicationProfile", // join field
            "id",          // field of ApplicationProfileEntity
            applicationProfileId
        );

        return entities.stream()
            .map(configVersionMapper::convertEntityToDto)
            .collect(Collectors.toList());
    }

    private String calculateHash(String json) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }

    @Async
    public void saveSnapshot(Long applicationProfileId, String usernamePlaceholder, String versionNote) {
        ApplicationProfileEntity profile = applicationProfileRepository.findById(applicationProfileId)
            .orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + applicationProfileId));

        List<ConfigPropertyEntity> configs = configPropertyRepository.findAllByApplicationProfileId(applicationProfileId);

        // Chuyển danh sách thành JSON string
        String snapshotJson;
        try {
            // Convert entities to DTOs first to avoid circular references
            List<ConfigPropertySnapshotDto> configDtos = configs.stream()
                .map(configPropertyMapper::convertEntityToSnapshotDto)
                .collect(Collectors.toList());
            // Use the already injected objectMapper
            snapshotJson = objectMapper.writeValueAsString(configDtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing config snapshot", e);
        }

        // Validate JSON length (optional - prevent too large JSON)
        if (snapshotJson.length() > 1000000) { // 1MB limit
            throw new RuntimeException("Config snapshot too large");
        }
        
        String snapshotHash = calculateHash(snapshotJson);
        log.info("Hash = {}, Length = {}", snapshotHash, snapshotHash.length());

        List<ConfigVersionEntity> configVersionEntities = configVersionRepository.findAll();

        for (ConfigVersionEntity configVersionEntity : configVersionEntities) {
            if (snapshotHash.equals(configVersionEntity.getSnapshotHash())) {
                log.warn("Found the same config backup with hash: " + snapshotHash + ", ignoring...");
                return;
            }
        }

        // Tạo bản ghi snapshot
        ConfigVersionEntity version = new ConfigVersionEntity();
        version.setApplicationProfile(profile);
        version.setConfigSnapshot(snapshotJson);
        version.setSnapshotHash(snapshotHash);
        version.setVersionNote(versionNote);
        version.setCreatedBy(usernamePlaceholder); // Placeholder
        version.setCreatedAt(LocalDateTime.now());

        try {
            configVersionRepository.save(version);
            log.info("Config snapshot saved successfully for profile {}", applicationProfileId);
        } catch (Exception e) {
            log.error("Error saving config snapshot for profile {}", applicationProfileId, e);
            throw new RuntimeException("Error saving config snapshot", e);
        }
    }

    @Transactional
    public List<ConfigPropertyDto> restoreSnapshot(Long configVersionId) {
        ConfigVersionEntity configVersionEntity = configVersionRepository.findById(configVersionId)
            .orElseThrow(() -> new EntityNotFoundException("Version not found with id: " + configVersionId));

        Long applicationProfileId = configVersionEntity.getApplicationProfile().getId();
        
        criteriaQueryHelper.deleteByJoinFilter(entityManager, ConfigPropertyEntity.class, "applicationProfile", "id", applicationProfileId);


        // Deserialize configSnapshot
        List<Map<String, Object>> configs;
        try {
            configs = objectMapper.readValue(
                configVersionEntity.getConfigSnapshot(), new TypeReference<List<Map<String, Object>>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing config snapshot", e);
        }

        List<ConfigPropertyEntity> restoredEntities = configs.stream().map(item -> {
            ConfigPropertyEntity entity = new ConfigPropertyEntity();
            entity.setApplicationProfile(configVersionEntity.getApplicationProfile());
            entity.setKey((String) item.get("key"));
            entity.setValue((String) item.get("value"));
            entity.setFormat((String) item.get("format"));
            entity.setDescription((String) item.get("description"));
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }).toList();
        
        List<ConfigPropertyEntity> savingEntities = configPropertyRepository.saveAll(restoredEntities);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
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

        return savingEntities.stream().map(configPropertyMapper::convertEntityToDto).collect(Collectors.toList());
    }
}
