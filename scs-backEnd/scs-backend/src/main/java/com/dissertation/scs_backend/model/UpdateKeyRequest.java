package com.dissertation.scs_backend.model;

import lombok.Data;

@Data
public class UpdateKeyRequest {
    private String email;
    private String publicKey;
}
