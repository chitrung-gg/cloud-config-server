package com.viettel.spring.cloud.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.spring.cloud.server.dto.configproperty.ConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.CreateConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.UpdateConfigPropertyDto;
import com.viettel.spring.cloud.server.service.ConfigPropertyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/config-properties")
@RequiredArgsConstructor
public class ConfigPropertyController {
    @Autowired
    private final ConfigPropertyService configPropertyService;

    @GetMapping
    public ResponseEntity<List<ConfigPropertyDto>> getAllConfigProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(configPropertyService.getAllConfigProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfigPropertyDto> getConfigPropertyById(@PathVariable Long id) {
        return ResponseEntity.of(configPropertyService.getConfigPropertyById(id));
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<ConfigPropertyDto> getConfigPropertyByKey(@PathVariable String key) {
        return ResponseEntity.of(configPropertyService.getConfigPropertyByKey(key));
    }

    @GetMapping("/format/{format}")
    public ResponseEntity<List<ConfigPropertyDto>> getConfigPropertyByFormat(@PathVariable String format) {
        return ResponseEntity.status(HttpStatus.OK).body(configPropertyService.getConfigPropertyByFormat(format));
    }

    @GetMapping("/value/{value}")
    public ResponseEntity<List<ConfigPropertyDto>> getConfigPropertyByValue(@PathVariable String value) {
        return ResponseEntity.status(HttpStatus.OK).body(configPropertyService.getConfigPropertyByValue(value));
    }

    @PostMapping
    public ResponseEntity<CreateConfigPropertyDto> createConfigProperty (@RequestBody @Valid CreateConfigPropertyDto createConfigPropertyDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configPropertyService.createConfigProperty(createConfigPropertyDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateConfigPropertyDto> updateConfigProperty(@PathVariable Long id, @RequestBody @Valid UpdateConfigPropertyDto updateConfigPropertyDto) {
        return ResponseEntity.of(configPropertyService.updateConfigProperty(id, updateConfigPropertyDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ConfigPropertyDto> deleteConfigProperty(@PathVariable Long id) {
        return ResponseEntity.of(configPropertyService.deleteConfigProperty(id));
    }

    @GetMapping("/application-profile/{applicationProfileId}")
    public ResponseEntity<List<ConfigPropertyDto>> getProfilesByApplicationProfileId(@PathVariable Long applicationProfileId) {
        return ResponseEntity.status(HttpStatus.OK).body(configPropertyService.findConfigPropertyByApplicationProfileId(applicationProfileId));
    }
}
