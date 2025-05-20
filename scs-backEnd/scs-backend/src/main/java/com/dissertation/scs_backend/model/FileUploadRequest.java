package com.dissertation.scs_backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadRequest {
    private String ownerEmail;
    private String key;
    private String iv;
    private String fileName;
    private byte[] fileData;
}