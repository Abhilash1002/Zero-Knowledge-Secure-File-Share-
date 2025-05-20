package com.dissertation.scs_backend.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.dissertation.scs_backend.model.Role;
import java.util.Collection;

class UserTest {

    @Test
    void testUserCreation() {
        User user = User.builder()
                .id(1)
                .userName("testUser")
                .email("test@example.com")
                .password("password123")
                .publicKey("publicKeyString")
                .role(Role.USER)
                .build();

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("publicKeyString", user.getPublicKey());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testUserSettersAndGetters() {
        User user = new User();
        user.setId(2);
        user.setUserName("updatedUser");
        user.setEmail("updated@example.com");
        user.setPassword("newPassword");
        user.setPublicKey("updatedPublicKey");
        user.setRole(Role.ADMIN);

        assertEquals(2, user.getId());
        assertEquals("updated@example.com", user.getUsername());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertEquals("updatedPublicKey", user.getPublicKey());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void testUserDetailsImplementation() {
        User user = User.builder()
                .email("user@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        assertEquals("user@example.com", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testGetAuthorities() {
        User user = User.builder()
                .role(Role.ADMIN)
                .build();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void testGetUserTag() {
        User user = User.builder()
                .userName("testUser")
                .build();

        assertEquals("testUser", user.getUserTag());
    }

    @Test
    void testUserEquality() {
        User user1 = User.builder()
                .id(1)
                .userName("testUser")
                .email("test@example.com")
                .password("password123")
                .publicKey("publicKeyString")
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .id(1)
                .userName("testUser")
                .email("test@example.com")
                .password("password123")
                .publicKey("publicKeyString")
                .role(Role.USER)
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserToString() {
        User user = User.builder()
                .id(1)
                .userName("testUser")
                .email("test@example.com")
                .password("password123")
                .publicKey("publicKeyString")
                .role(Role.USER)
                .build();

        String toStringResult = user.toString();

        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("userName=test@example.com"));
        assertTrue(toStringResult.contains("email=test@example.com"));
        assertTrue(toStringResult.contains("password=password123"));
        assertTrue(toStringResult.contains("publicKey=publicKeyString"));
        assertTrue(toStringResult.contains("role=USER"));
    }
}