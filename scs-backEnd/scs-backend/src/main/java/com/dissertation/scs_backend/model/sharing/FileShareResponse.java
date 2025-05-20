package com.dissertation.scs_backend.model.sharing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileShareResponse {
    private Integer shareId;
    private Integer fileId;
    private String senderEmail;
    private String receiverEmail;
    private String expiry;
}