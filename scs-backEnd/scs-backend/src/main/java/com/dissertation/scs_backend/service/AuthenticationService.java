package com.dissertation.scs_backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dissertation.scs_backend.entity.User;
import com.dissertation.scs_backend.model.LoginRequest;
import com.dissertation.scs_backend.model.LoginResponse;
import com.dissertation.scs_backend.model.RegisterRequest;
import com.dissertation.scs_backend.model.RegisterResponse;
import com.dissertation.scs_backend.model.Role;
import com.dissertation.scs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public RegisterResponse register(RegisterRequest request) {
        var user = User.builder()
            .userName(request.getUserName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)   
            .build();

        var createdUser = userRepository.save(user);
        
        return RegisterResponse.builder()
            .email(createdUser.getEmail())
            .id(createdUser.getId())
            .msg("Account successfully created")
            .build();
    }

    public LoginResponse login(LoginRequest request) {
        // check if username and password are correct
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
            .token(jwtToken)
            .id(user.getId())
            .email(user.getEmail())
            .publicKey(user.getPublicKey())
            .userName(user.getUserTag())
            .build();
    }
}
