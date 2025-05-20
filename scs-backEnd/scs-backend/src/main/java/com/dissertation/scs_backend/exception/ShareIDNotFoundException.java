package com.dissertation.scs_backend.exception;

public class ShareIDNotFoundException extends RuntimeException{
    public ShareIDNotFoundException(String message){
        super(message);
    }
}
