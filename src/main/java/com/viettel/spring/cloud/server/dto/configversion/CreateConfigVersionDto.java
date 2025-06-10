package com.viettel.spring.cloud.server.dto.configversion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateConfigVersionDto {
    @NotNull
    private Long applicationProfileId;

    @NotBlank
    private String versionNote;
}
