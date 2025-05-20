package com.dissertation.scs_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Lob
    @Column(name = "`key`", nullable = false, columnDefinition = "TEXT")
    private String key;

    @Lob
    @Column(name = "IV", nullable = false, columnDefinition = "TEXT")
    private String iv;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

}