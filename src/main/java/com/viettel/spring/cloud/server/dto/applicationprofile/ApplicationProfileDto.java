package com.viettel.spring.cloud.server.dto.applicationprofile;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.spring.cloud.server.dto.application.ApplicationDto;

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
public class ApplicationProfileDto {
    private Long id;

    private ApplicationDto application;

    @NotBlank
    private String profile;

    @NotBlank
    private String label;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
