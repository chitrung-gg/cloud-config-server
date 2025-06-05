package com.viettel.spring.cloud.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.spring.cloud.server.dto.configproperty.ConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configversion.ConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.CreateConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.UpdateConfigVersionDto;
import com.viettel.spring.cloud.server.service.ConfigVersionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/config-versions")
@RequiredArgsConstructor
public class ConfigVersionController {
    @Autowired
    private final ConfigVersionService configVersionService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'READ')")
    public ResponseEntity<List<ConfigVersionDto>> getAllConfigProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(configVersionService.getAllConfigProperties());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'READ')")
    public ResponseEntity<ConfigVersionDto> getConfigVersionById(@PathVariable Long id) {
        return ResponseEntity.of(configVersionService.getConfigVersionById(id));
    }

    @PostMapping
    public ResponseEntity<CreateConfigVersionDto> createConfigVersion (@RequestBody @Valid CreateConfigVersionDto createConfigVersionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configVersionService.createConfigVersion(createConfigVersionDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateConfigVersionDto> updateConfigVersion(@PathVariable Long id, @RequestBody @Valid UpdateConfigVersionDto updateConfigVersionDto) {
        return ResponseEntity.of(configVersionService.updateConfigVersion(id, updateConfigVersionDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ConfigVersionDto> deleteConfigVersion(@PathVariable Long id) {
        return ResponseEntity.of(configVersionService.deleteConfigVersion(id));
    }

    @GetMapping("/application-profile/{applicationProfileId}")
    public ResponseEntity<List<ConfigVersionDto>> getProfilesByApplicationProfileId(@PathVariable Long applicationProfileId) {
        return ResponseEntity.status(HttpStatus.OK).body(configVersionService.findConfigVersionByApplicationProfileId(applicationProfileId));
    }

    @PostMapping("/{versionId}/restore")
    public ResponseEntity<List<ConfigPropertyDto>> restoreVersion(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.OK).body(configVersionService.restoreSnapshot(versionId));
    }
}
