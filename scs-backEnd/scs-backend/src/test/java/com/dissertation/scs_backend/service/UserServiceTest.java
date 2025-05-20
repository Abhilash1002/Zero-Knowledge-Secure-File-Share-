package com.dissertation.scs_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dissertation.scs_backend.entity.User;
import com.dissertation.scs_backend.exception.UserNotFoundException;
import com.dissertation.scs_backend.model.UserInfo;
import com.dissertation.scs_backend.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePublicKey_UserFound() throws UserNotFoundException {
        // Arrange
        String email = "test@example.com";
        String publicKey = "newPublicKey";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        userService.updatePublicKey(email, publicKey);

        // Assert
        assertEquals(publicKey, user.getPublicKey());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdatePublicKey_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String publicKey = "newPublicKey";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updatePublicKey(email, publicKey));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUserExists_ByEmail() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setUserName("testuser");
        user.setPublicKey("publicKey");
        when(userRepository.findByEmailOrUserName(email, email)).thenReturn(Optional.of(user));

        // Act
        UserInfo result = userService.userExists(email);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(email, result.getEmail());
        assertEquals("testuser", result.getUserName());
        assertEquals("publicKey", result.getPublicKey());
    }

    @Test
    void testUserExists_ByUserName() {
        // Arrange
        String userName = "testuser";
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUserName(userName);
        user.setPublicKey("publicKey");
        when(userRepository.findByEmailOrUserName(userName, userName)).thenReturn(Optional.of(user));

        // Act
        UserInfo result = userService.userExists(userName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(userName, result.getUserName());
        assertEquals("publicKey", result.getPublicKey());
    }

    @Test
    void testUserExists_UserNotFound() {
        // Arrange
        String identifier = "nonexistent";
        when(userRepository.findByEmailOrUserName(identifier, identifier)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.userExists(identifier));
    }
}