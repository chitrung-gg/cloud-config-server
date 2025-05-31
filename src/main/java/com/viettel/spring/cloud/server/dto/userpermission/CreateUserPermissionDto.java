package com.viettel.spring.cloud.server.dto.userpermission;

import java.util.List;

import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserPermissionDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long applicationProfileId;

    @NotNull
    private List<UserPermissionEntity.Permission> permission;
}
