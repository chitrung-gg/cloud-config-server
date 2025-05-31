package com.viettel.spring.cloud.server.dto.configproperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigPropertyDto {
    private Long applicationProfileId;

    private String key;
    
    private String value;

    private String format;
    
    // @Column(name = "encrypted")
    // private Boolean encrypted = false;
    
    private String description;
}
