package com.viettel.spring.cloud.server.dto.application;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
