package com.viettel.spring.cloud.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity, Long> {
    
}
