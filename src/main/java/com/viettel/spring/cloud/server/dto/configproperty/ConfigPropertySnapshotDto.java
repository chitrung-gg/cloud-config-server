package com.viettel.spring.cloud.server.dto.configproperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigPropertySnapshotDto {
    private Long id;

    private Long applicationProfileId;

    @NotBlank
    private String key;
    
    @NotBlank
    private String value;

    @NotBlank
    private String format;

    @NotBlank
    private String description;
}
