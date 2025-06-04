package com.viettel.spring.cloud.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.spring.cloud.server.dto.authentication.AuthenticationRequestDto;
import com.viettel.spring.cloud.server.dto.authentication.AuthenticationResponseDto;
import com.viettel.spring.cloud.server.dto.authentication.RegisterRequestDto;
import com.viettel.spring.cloud.server.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody @Valid AuthenticationRequestDto authenticationRequestDto) {
        return ResponseEntity.of(authenticationService.login(authenticationRequestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterRequestDto> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        return ResponseEntity.of(authenticationService.register(registerRequestDto));
    }
    
    

}
