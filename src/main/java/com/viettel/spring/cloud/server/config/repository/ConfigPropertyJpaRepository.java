package com.viettel.spring.cloud.server.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;

@Repository
public interface ConfigPropertyJpaRepository extends JpaRepository<ConfigPropertyEntity, Long> {
    List<ConfigPropertyEntity> findByApplicationProfile_Application_NameAndApplicationProfile_ProfileAndApplicationProfile_Label(String applicationName, String profile, String label);

}
