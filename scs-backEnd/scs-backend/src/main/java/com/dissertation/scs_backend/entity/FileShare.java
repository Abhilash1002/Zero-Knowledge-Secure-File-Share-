package com.dissertation.scs_backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_shares")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private Integer shareId;

    @Column(name = "file_id", nullable = false)
    private Integer fileId;

    @Column(name = "sender", nullable = false)
    private Integer sender;

    @Column(name = "`receiver`", nullable = false)
    private Integer receiver;

    @Lob
    @Column(name = "`key`", nullable = false, columnDefinition = "TEXT")
    private String key;

    @Lob
    @Column(name = "IV", nullable = false, columnDefinition = "TEXT")
    private String iv;

    @Column(name = "expiry")
    private String expiry;

}