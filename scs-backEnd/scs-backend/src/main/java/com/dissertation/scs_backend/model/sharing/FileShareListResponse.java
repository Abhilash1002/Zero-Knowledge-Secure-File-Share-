package com.dissertation.scs_backend.model.sharing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileShareListResponse {
    private Integer shareId;
    private Integer fileId;
    private String fileName;
    private String sender;
    private String receiver;
    private String expiry;
}
