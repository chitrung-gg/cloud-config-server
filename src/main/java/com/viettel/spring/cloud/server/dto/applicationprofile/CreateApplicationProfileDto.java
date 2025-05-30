package com.viettel.spring.cloud.server.dto.applicationprofile;

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
public class CreateApplicationProfileDto {
    @NotNull
    private Long applicationId;
    
    @NotBlank
    private String profile;

    @NotBlank
    private String label;
}
