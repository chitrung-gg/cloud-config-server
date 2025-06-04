package com.viettel.spring.cloud.server.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDto {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}