package com.dissertation.scs_backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileContentResponse {
    private String fileName;
    private String key;
    private String iv;
    private byte[] fileData;
}
