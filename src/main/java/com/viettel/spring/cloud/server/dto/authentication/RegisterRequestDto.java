package com.viettel.spring.cloud.server.dto.authentication;

import com.viettel.spring.cloud.server.entity.UserEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank
    private String fullName;
    
    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotNull
    private UserEntity.Role role;

    @NotBlank
    private String password;
}
