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

import com.viettel.spring.cloud.server.dto.applicationprofile.ApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.CreateApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.UpdateApplicationProfileDto;
import com.viettel.spring.cloud.server.service.ApplicationProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/application-profiles")
@RequiredArgsConstructor
public class ApplicationProfileController {
    @Autowired
    private final ApplicationProfileService applicationProfileService;

    @GetMapping
    public ResponseEntity<List<ApplicationProfileDto>> getAllApplicationProfiles() {
        return ResponseEntity.status(HttpStatus.OK).body(applicationProfileService.getAllApplicationProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationProfileDto> getApplicationProfileById(@PathVariable Long id) {
        return ResponseEntity.of(applicationProfileService.getApplicationProfileById(id));
    }

    @GetMapping("/label/{label}")
    public ResponseEntity<List<ApplicationProfileDto>> getApplicationProfileByLabel(@PathVariable String label) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationProfileService.getApplicationProfileByLabel(label));
    }

    @GetMapping("/profile/{profile}")
    public ResponseEntity<List<ApplicationProfileDto>> getApplicationProfileByProfile(@PathVariable String profile) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationProfileService.getApplicationProfileByProfile(profile));
    }

    @PostMapping
    public ResponseEntity<CreateApplicationProfileDto> createApplicationProfile (@RequestBody @Valid CreateApplicationProfileDto createApplicationProfileDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationProfileService.createApplicationProfile(createApplicationProfileDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateApplicationProfileDto> updateApplicationProfile(@PathVariable Long id, @RequestBody @Valid UpdateApplicationProfileDto updateApplicationProfileDto) {
        return ResponseEntity.of(applicationProfileService.updateApplicationProfile(id, updateApplicationProfileDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationProfileDto> deleteApplicationProfile(@PathVariable Long id) {
        return ResponseEntity.of(applicationProfileService.deleteApplicationProfile(id));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ApplicationProfileDto>> getProfilesByApplicationId(@PathVariable Long applicationId) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationProfileService.findProfilesByApplicationId(applicationId));
    }
}
