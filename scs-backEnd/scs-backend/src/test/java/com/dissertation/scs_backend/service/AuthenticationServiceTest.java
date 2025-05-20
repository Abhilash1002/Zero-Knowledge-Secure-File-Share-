package com.dissertation.scs_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dissertation.scs_backend.entity.User;
import com.dissertation.scs_backend.model.LoginRequest;
import com.dissertation.scs_backend.model.LoginResponse;
import com.dissertation.scs_backend.model.RegisterRequest;
import com.dissertation.scs_backend.model.RegisterResponse;
import com.dissertation.scs_backend.model.Role;
import com.dissertation.scs_backend.repository.UserRepository;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("testUser", "test@example.com", "password");
        User savedUser = User.builder()
                .id(1)
                .userName("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        RegisterResponse response = authenticationService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals(1, response.getId());
        assertEquals("Account successfully created", response.getMsg());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password");
        User user = User.builder()
                .id(1)
                .userName("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .publicKey("publicKey")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Act
        LoginResponse response = authenticationService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(1, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("publicKey", response.getPublicKey());
        assertEquals("testUser", response.getUserName());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail("test@example.com");
        verify(jwtService).generateToken(user);
    }

    @Test
    void testRegister_DuplicateEmail() {
        // Arrange
        RegisterRequest request = new RegisterRequest("testUser", "existing@example.com", "password");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Duplicate email"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.register(request));

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.login(request));

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
}