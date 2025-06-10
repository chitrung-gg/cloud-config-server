package com.viettel.spring.cloud.server.dto.applicationmetadata;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationMetadataDto {
    @NotNull
    private Long applicationId;

    private String owner;

    private String team;

    private String environment;

    private String category;

    private String criticality;

    private String documentation;

    private String repository;

    private String contact;

    private String businessUnit;

    private String costCenter;

    private String maintenanceWindow;

    private List<String> tags;
}
