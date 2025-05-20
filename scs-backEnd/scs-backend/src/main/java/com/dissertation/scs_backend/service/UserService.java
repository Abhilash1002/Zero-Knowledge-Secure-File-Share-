package com.dissertation.scs_backend.service;
import java.util.logging.Logger;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dissertation.scs_backend.entity.User;
import com.dissertation.scs_backend.exception.UserNotFoundException;
import com.dissertation.scs_backend.model.UserInfo;
import com.dissertation.scs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    Logger logger = Logger.getLogger(getClass().getName());

    public void updatePublicKey(String email, String publicKey) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info(publicKey);
            user.setPublicKey(publicKey);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
    }

    public UserInfo userExists(String identifier) {
        User user = userRepository.findByEmailOrUserName(identifier, identifier).orElseThrow(() -> new UserNotFoundException("So such user exists"));
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserTag())
                .publicKey(user.getPublicKey())
                .build();
    }
}
