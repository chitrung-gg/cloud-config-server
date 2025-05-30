package com.viettel.spring.cloud.server.dto.applicationprofile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationProfileDto {
    private Long applicationId;
    
    private String profile;

    private String label;
}
