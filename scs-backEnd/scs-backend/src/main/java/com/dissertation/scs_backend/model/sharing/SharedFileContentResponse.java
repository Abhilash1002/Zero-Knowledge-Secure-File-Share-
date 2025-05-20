package com.dissertation.scs_backend.model.sharing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedFileContentResponse {
    private String fileName;
    private String key;
    private String iv;
    private byte[] fileData;
}