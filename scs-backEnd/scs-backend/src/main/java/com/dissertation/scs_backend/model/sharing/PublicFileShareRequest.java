package com.dissertation.scs_backend.model.sharing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicFileShareRequest {
    private String ownerEmail;
    private Integer fileId;
    private String fileName;
    private byte[] fileData;
}