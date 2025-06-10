package com.viettel.spring.cloud.server.dto.configversion;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.spring.cloud.server.dto.applicationprofile.ApplicationProfileDto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVersionDto {
    private Long id;

    private ApplicationProfileDto applicationProfile;

    @NotBlank
    private String configSnapshot;

    @NotBlank
    private String versionNote;

    @NotBlank 
    private String createdBy;

    private String snapshotHash;

    private Boolean isNewlyCreated;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
