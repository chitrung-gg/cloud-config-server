package com.viettel.spring.cloud.server.dto.configproperty;

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
public class CreateConfigPropertyDto {
    @NotNull
    private Long applicationProfileId;

    @NotBlank
    private String key;
    
    @NotBlank
    private String value;

    @NotBlank
    private String format;
    
    // @Column(name = "encrypted")
    // private Boolean encrypted = false;
    
    @NotBlank
    private String description;
}
