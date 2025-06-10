package com.viettel.spring.cloud.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.ApplicationMetadataEntity;

@Repository
public interface ApplicationMetadataRepository extends JpaRepository<ApplicationMetadataEntity, Long> {
    
}
