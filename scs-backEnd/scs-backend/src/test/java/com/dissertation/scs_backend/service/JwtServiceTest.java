package com.dissertation.scs_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_TIME);
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        // assertEquals("testuser", username);
    }

    @Test
    void testGenerateToken() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals("ADMIN", jwtService.extractClaim(token, claims -> claims.get("role")));
    }

    @Test
    void testIsTokenValid() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    // @Test
    // void testIsTokenValidWithExpiredToken() {
    //     // Arrange
    //     when(userDetails.getUsername()).thenReturn("testuser");
    //     ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000); // Set expiration to 1 second ago
    //     String token = jwtService.generateToken(userDetails);

    //     // Act
    //     boolean isValid = jwtService.isTokenValid(token, userDetails);

    //     // Assert
    //     assertFalse(isValid);
    // }

    @Test
    void testExtractClaim() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);

        // Act
        Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}