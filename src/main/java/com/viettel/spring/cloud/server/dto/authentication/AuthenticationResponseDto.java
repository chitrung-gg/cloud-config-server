package com.viettel.spring.cloud.server.dto.authentication;

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
public class AuthenticationResponseDto {
    @NotBlank
    private String accessToken;
    
    @NotNull
    private Long expiryTime;

    @NotBlank
    private String tokenType = "Bearer";
}
