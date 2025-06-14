package com.viettel.spring.cloud.server.dto.configproperty;

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
public class ConfigPropertyDto {
    private Long id;

    private ApplicationProfileDto applicationProfile;

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

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
