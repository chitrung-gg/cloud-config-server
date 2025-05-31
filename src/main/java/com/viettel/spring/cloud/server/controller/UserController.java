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

import com.viettel.spring.cloud.server.dto.user.UserDto;
import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.dto.user.CreateUserDto;
import com.viettel.spring.cloud.server.dto.user.UpdateUserDto;
import com.viettel.spring.cloud.server.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.of(userService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.of(userService.getUserByUsername(username));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUserByRole(@PathVariable UserEntity.Role role) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByRole(role));
    }

    @PostMapping
    public ResponseEntity<CreateUserDto> createUser (@RequestBody @Valid CreateUserDto createUserDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateUserDto> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserDto updateUserDto) {
        return ResponseEntity.of(userService.updateUser(id, updateUserDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
        return ResponseEntity.of(userService.deleteUser(id));
    }

    // @GetMapping("/application-profile/{applicationProfileId}")
    // public ResponseEntity<List<UserDto>> getProfilesByApplicationProfileId(@PathVariable Long applicationProfileId) {
    //     return ResponseEntity.status(HttpStatus.OK).body(UserService.findUserByApplicationProfileId(applicationProfileId));
    // }
}
