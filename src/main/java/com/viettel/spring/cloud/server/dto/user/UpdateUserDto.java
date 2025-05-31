package com.viettel.spring.cloud.server.dto.user;

import com.viettel.spring.cloud.server.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String username;

    private String password;

    private String fullName;

    private String email;

    private UserEntity.Role role;
}
