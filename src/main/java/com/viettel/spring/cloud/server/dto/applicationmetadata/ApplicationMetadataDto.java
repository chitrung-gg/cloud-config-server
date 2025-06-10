package com.viettel.spring.cloud.server.dto.applicationmetadata;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMetadataDto {
    @NotNull
    private Long id;

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

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
