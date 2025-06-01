package com.viettel.spring.cloud.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigPropertyRepository extends JpaRepository<ConfigPropertyEntity, Long> {
    Optional<ConfigPropertyEntity> findByKey(String key);
    List<ConfigPropertyEntity> findByValue(String value);
    List<ConfigPropertyEntity> findByFormat(String format);
    List<ConfigPropertyEntity> findAllByApplicationProfileId(Long profileId);
}
