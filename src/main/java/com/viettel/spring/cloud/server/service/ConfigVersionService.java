package com.viettel.spring.cloud.server.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.spring.cloud.server.dto.configversion.ConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.CreateConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.UpdateConfigVersionDto;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;
import com.viettel.spring.cloud.server.entity.ConfigVersionEntity;
import com.viettel.spring.cloud.server.helper.CriteriaQueryHelper;
import com.viettel.spring.cloud.server.mapper.ConfigVersionMapper;
import com.viettel.spring.cloud.server.repository.ApplicationProfileRepository;
import com.viettel.spring.cloud.server.repository.ConfigPropertyRepository;
import com.viettel.spring.cloud.server.repository.ConfigVersionRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final CriteriaQueryHelper criteriaQueryHelper;

    @Autowired
    private final EntityManager entityManager;

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

    
    public void saveSnapshot(Long profileId, String usernamePlaceholder, String versionNote) {
        ApplicationProfileEntity profile = applicationProfileRepository.findById(profileId)
            .orElseThrow(() -> new EntityNotFoundException("ApplicationProfile not found with id: " + profileId));

        List<ConfigPropertyEntity> configs = configPropertyRepository.findAllByApplicationProfileId(profileId);

        // Tạo danh sách các config chi tiết
        List<Map<String, String>> configList = configs.stream().map(cfg -> {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("applicationProfileId", cfg.getApplicationProfile().toString());
            map.put("key", cfg.getKey());
            map.put("value", cfg.getValue());
            map.put("format", cfg.getFormat());
            map.put("description", cfg.getDescription());
            return map;
        }).collect(Collectors.toList());

        // Chuyển danh sách thành JSON string
        String snapshotJson;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            snapshotJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(configList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing config snapshot", e);
        }

        // Validate JSON length (optional - prevent too large JSON)
        if (snapshotJson.length() > 1000000) { // 1MB limit
            throw new RuntimeException("Config snapshot too large");
        }

        // Tạo bản ghi snapshot
        ConfigVersionEntity version = new ConfigVersionEntity();
        version.setApplicationProfile(profile);
        version.setConfigSnapshot(snapshotJson);
        version.setVersionNote(versionNote);
        version.setCreatedBy(usernamePlaceholder); // Placeholder
        version.setCreatedAt(LocalDateTime.now());

        try {
            configVersionRepository.save(version);
            log.info("Config snapshot saved successfully for profile {}", profileId);
        } catch (Exception e) {
            log.error("Error saving config snapshot for profile {}", profileId, e);
            throw new RuntimeException("Error saving config snapshot", e);
        }
    }
}
