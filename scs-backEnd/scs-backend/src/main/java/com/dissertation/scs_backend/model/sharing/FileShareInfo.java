package com.dissertation.scs_backend.model.sharing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareInfo {
    private Integer shareId;
    private Integer fileId;
    private String userName;
    private String email;
    private String expiry;
}
