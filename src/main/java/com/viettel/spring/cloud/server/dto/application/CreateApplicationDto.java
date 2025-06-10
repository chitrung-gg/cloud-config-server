package com.viettel.spring.cloud.server.dto.application;


import com.viettel.spring.cloud.server.dto.applicationmetadata.ApplicationMetadataDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;    

    @NotBlank
    private String version;

    // @NotNull
    // private List<String> tags;
    private ApplicationMetadataDto metadata;
}
