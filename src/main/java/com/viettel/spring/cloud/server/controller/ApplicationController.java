package com.viettel.spring.cloud.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.spring.cloud.server.dto.application.ApplicationDto;
import com.viettel.spring.cloud.server.dto.application.CreateApplicationDto;
import com.viettel.spring.cloud.server.dto.application.UpdateApplicationDto;
import com.viettel.spring.cloud.server.service.ApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {
    @Autowired
    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.of(applicationService.getApplicationById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApplicationDto> getApplicationByName(@PathVariable String name) {
        return ResponseEntity.of(applicationService.getApplicationByName(name));
    }

    @PostMapping
    public ResponseEntity<CreateApplicationDto> createApplication (@RequestBody @Valid CreateApplicationDto createApplicationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(createApplicationDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateApplicationDto> updateApplication(@PathVariable Long id, @RequestBody @Valid UpdateApplicationDto updateApplicationDto) {
        return ResponseEntity.of(applicationService.updateApplication(id, updateApplicationDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationDto> deleteApplication(@PathVariable Long id) {
        return ResponseEntity.of(applicationService.deleteApplication(id));
    }
    
}
