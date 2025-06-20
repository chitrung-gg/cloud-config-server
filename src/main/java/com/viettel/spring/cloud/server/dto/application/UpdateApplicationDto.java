package com.viettel.spring.cloud.server.dto.application;


import com.viettel.spring.cloud.server.dto.applicationmetadata.ApplicationMetadataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationDto {
    private String name;

    private String description;

    private String version;

    // private List<String> tags;
    private ApplicationMetadataDto metadata;
}
