package com.viettel.spring.cloud.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.ConfigVersionEntity;

@Repository
public interface ConfigVersionRepository extends JpaRepository<ConfigVersionEntity, Long> {
    List<ConfigVersionEntity> findByApplicationProfileIdOrderByCreatedAtDesc(Long applicationProfileId);
    Optional<ConfigVersionEntity> findBySnapshotHash(String snapshotHash);
}
