package com.viettel.spring.cloud.server.dto.userpermission;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.spring.cloud.server.dto.applicationprofile.ApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.user.UserDto;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionDto {
    private Long id;

    private UserDto user;
    private ApplicationProfileDto applicationProfile;

    @NotNull
    private UserPermissionEntity.Permission permission;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
