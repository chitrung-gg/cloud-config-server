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

import com.viettel.spring.cloud.server.dto.userpermission.UserPermissionDto;
import com.viettel.spring.cloud.server.dto.userpermission.CreateUserPermissionDto;
import com.viettel.spring.cloud.server.dto.userpermission.UpdateUserPermissionDto;
import com.viettel.spring.cloud.server.service.UserPermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-permissions")
@RequiredArgsConstructor
public class UserPermissionController {
    @Autowired
    private final UserPermissionService userPermissionService;

    @GetMapping
    public ResponseEntity<List<UserPermissionDto>> getAllConfigProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(userPermissionService.getAllConfigProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPermissionDto> getUserPermissionById(@PathVariable Long id) {
        return ResponseEntity.of(userPermissionService.getUserPermissionById(id));
    }

    @PostMapping
    public ResponseEntity<CreateUserPermissionDto> createUserPermission (@RequestBody @Valid CreateUserPermissionDto createUserPermissionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPermissionService.createUserPermission(createUserPermissionDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateUserPermissionDto> updateUserPermission(@PathVariable Long id, @RequestBody @Valid UpdateUserPermissionDto updateUserPermissionDto) {
        return ResponseEntity.of(userPermissionService.updateUserPermission(id, updateUserPermissionDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<UserPermissionDto> deleteUserPermission(@PathVariable Long id) {
        return ResponseEntity.of(userPermissionService.deleteUserPermission(id));
    }

    @GetMapping("/application-profile/{applicationProfileId}")
    public ResponseEntity<List<UserPermissionDto>> getProfilesByApplicationProfileId(@PathVariable Long applicationProfileId) {
        return ResponseEntity.status(HttpStatus.OK).body(userPermissionService.findUserPermissionByApplicationProfileId(applicationProfileId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPermissionDto>> getProfilesByUserId(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userPermissionService.findUserPermissionByUserId(userId));
    }
}
