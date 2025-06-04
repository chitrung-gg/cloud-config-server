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
public class AuthenticationResponseDto {
    @NotBlank
    private String accessToken;
    
    // @NotBlank
    // private String tokenType = "Bearer";
}
