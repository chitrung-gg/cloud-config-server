package com.viettel.spring.cloud.server.dto.userpermission;

import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPermissionDto {
    private Long userId;

    private Long applicationProfileId;

    private UserPermissionEntity.Permission permission;
}
