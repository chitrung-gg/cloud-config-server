package com.viettel.spring.cloud.server.dto.configversion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigVersionDto {
    private Long applicationProfileId;

    private String configSnapshot;

    private String versionNote;

    private String createdBy;
}