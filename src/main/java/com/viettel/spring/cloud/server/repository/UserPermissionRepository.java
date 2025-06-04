package com.viettel.spring.cloud.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;
import java.util.List;


@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity, Long> {
    List<UserPermissionEntity> findByUser(UserEntity user);  
}
