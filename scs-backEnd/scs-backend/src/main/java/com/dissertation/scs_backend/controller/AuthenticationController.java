package com.dissertation.scs_backend.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dissertation.scs_backend.model.LoginRequest;
import com.dissertation.scs_backend.model.LoginResponse;
import com.dissertation.scs_backend.model.RegisterRequest;
import com.dissertation.scs_backend.model.RegisterResponse;
import com.dissertation.scs_backend.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
        @RequestBody @Valid RegisterRequest request) {

            var res = authenticationService.register(request);
            URI uri = URI.create("/api/v1/auth/register/" + res.getId());
            return ResponseEntity.created(uri).body(res);
        }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
