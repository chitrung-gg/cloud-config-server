package com.viettel.spring.cloud.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;
import java.util.List;


@Repository
public interface ApplicationProfileRepository extends JpaRepository<ApplicationProfileEntity, Long> {
    List<ApplicationProfileEntity> findByProfile(String profile);
    List<ApplicationProfileEntity> findByLabel(String label);
}
