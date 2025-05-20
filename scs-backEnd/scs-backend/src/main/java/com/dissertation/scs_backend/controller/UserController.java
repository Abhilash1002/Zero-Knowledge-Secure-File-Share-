package com.dissertation.scs_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dissertation.scs_backend.exception.UserNotFoundException;
import com.dissertation.scs_backend.model.UpdateKeyRequest;
import com.dissertation.scs_backend.model.UserInfo;
import com.dissertation.scs_backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/public-key")
    public ResponseEntity<String> updatePublicKey(
        @RequestBody UpdateKeyRequest request
    ) {
        try {
            userService.updatePublicKey(request.getEmail(), request.getPublicKey());
            return ResponseEntity.ok("Public key updated successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the public key.");
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<UserInfo> checkUserExistence(@RequestParam String identifier) {
        try {
            UserInfo userInfo = userService.userExists(identifier);
            return ResponseEntity.ok().body(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
