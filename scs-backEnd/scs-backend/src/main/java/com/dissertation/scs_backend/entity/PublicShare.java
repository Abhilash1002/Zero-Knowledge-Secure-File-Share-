package com.dissertation.scs_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "public_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Integer fileId;

    @Column(name = "private_file_id", nullable = false)
    private Integer privateFileId;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "`file_name`", nullable = false)
    private String fileName;

    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;
}