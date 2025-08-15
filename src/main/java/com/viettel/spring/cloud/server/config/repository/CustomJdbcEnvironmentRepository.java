package com.viettel.spring.cloud.server.config.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomJdbcEnvironmentRepository implements EnvironmentRepository{
    @Autowired
    private final CustomEnvironmentService customEnvironmentService;

    @Override
    public Environment findOne(String applicationName, String profile, String label) {
        return customEnvironmentService.findOne(applicationName, profile, label);
        // String effectiveLabel = (label == null || label.isEmpty()) ? "main" : label;

        // List<ConfigPropertyEntity> configs = configPropertyRepository
        //     .findByApplicationProfile_Application_NameAndApplicationProfile_ProfileAndApplicationProfile_Label(
        //         applicationName, profile, effectiveLabel);

        // Map<String, Object> properties = new LinkedHashMap<>();
        // for (ConfigPropertyEntity config : configs) {
        //     properties.put(config.getKey(), config.getValue());
        // }

        // PropertySource propertySource = new PropertySource("custom-db-config", properties);

        // Environment env = new Environment(applicationName, new String[] { profile }, effectiveLabel, null, null);
        // env.add(propertySource);

        // return env;
    }
}
