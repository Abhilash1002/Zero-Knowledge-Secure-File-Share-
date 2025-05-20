package com.dissertation.scs_backend.exception;

public class UserFileNotFoundException extends RuntimeException {
    public UserFileNotFoundException(String message) {
        super(message);
    }
}