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

import com.viettel.spring.cloud.server.dto.applicationmetadata.ApplicationMetadataDto;
import com.viettel.spring.cloud.server.dto.applicationmetadata.CreateApplicationMetadataDto;
import com.viettel.spring.cloud.server.dto.applicationmetadata.UpdateApplicationMetadataDto;
import com.viettel.spring.cloud.server.service.ApplicationMetadataService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/application-metadatas")
@RequiredArgsConstructor
public class ApplicationMetadataController {
    @Autowired
    private final ApplicationMetadataService applicationMetadataService;

    @GetMapping
    public ResponseEntity<List<ApplicationMetadataDto>> getAllApplicationMetadatas() {
        return ResponseEntity.status(HttpStatus.OK).body(applicationMetadataService.getAllApplicationMetadatas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationMetadataDto> getApplicationMetadataById(@PathVariable Long id) {
        return ResponseEntity.of(applicationMetadataService.getApplicationMetadataById(id));
    }

    @PostMapping
    public ResponseEntity<CreateApplicationMetadataDto> createApplicationMetadata (@RequestBody @Valid CreateApplicationMetadataDto createApplicationMetadataDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationMetadataService.createApplicationMetadata(createApplicationMetadataDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateApplicationMetadataDto> updateApplicationMetadata(@PathVariable Long id, @RequestBody @Valid UpdateApplicationMetadataDto updateApplicationMetadataDto) {
        return ResponseEntity.of(applicationMetadataService.updateApplicationMetadata(id, updateApplicationMetadataDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationMetadataDto> deleteApplicationMetadata(@PathVariable Long id) {
        return ResponseEntity.of(applicationMetadataService.deleteApplicationMetadata(id));
    }
}
