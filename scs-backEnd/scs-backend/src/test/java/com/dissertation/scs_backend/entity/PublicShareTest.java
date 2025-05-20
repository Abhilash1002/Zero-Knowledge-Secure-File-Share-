package com.dissertation.scs_backend.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

class PublicShareTest {

    @Test
    void testPublicShareCreation() {
        byte[] testData = "Test file content".getBytes();
        PublicShare publicShare = PublicShare.builder()
                .fileId(1)
                .privateFileId(100)
                .ownerId(1001)
                .fileName("test.txt")
                .fileData(testData)
                .build();

        assertNotNull(publicShare);
        assertEquals(1, publicShare.getFileId());
        assertEquals(100, publicShare.getPrivateFileId());
        assertEquals(1001, publicShare.getOwnerId());
        assertEquals("test.txt", publicShare.getFileName());
        assertTrue(Arrays.equals(testData, publicShare.getFileData()));
    }

    @Test
    void testPublicShareSettersAndGetters() {
        PublicShare publicShare = new PublicShare();
        byte[] testData = "Updated file content".getBytes();

        publicShare.setFileId(2);
        publicShare.setPrivateFileId(200);
        publicShare.setOwnerId(2001);
        publicShare.setFileName("updated.txt");
        publicShare.setFileData(testData);

        assertEquals(2, publicShare.getFileId());
        assertEquals(200, publicShare.getPrivateFileId());
        assertEquals(2001, publicShare.getOwnerId());
        assertEquals("updated.txt", publicShare.getFileName());
        assertTrue(Arrays.equals(testData, publicShare.getFileData()));
    }

    @Test
    void testPublicShareEquality() {
        byte[] testData = "Test file content".getBytes();
        PublicShare publicShare1 = PublicShare.builder()
                .fileId(1)
                .privateFileId(100)
                .ownerId(1001)
                .fileName("test.txt")
                .fileData(testData)
                .build();

        PublicShare publicShare2 = PublicShare.builder()
                .fileId(1)
                .privateFileId(100)
                .ownerId(1001)
                .fileName("test.txt")
                .fileData(testData)
                .build();

        assertEquals(publicShare1, publicShare2);
        assertEquals(publicShare1.hashCode(), publicShare2.hashCode());
    }

    @Test
    void testPublicShareToString() {
        byte[] testData = "Test file content".getBytes();
        PublicShare publicShare = PublicShare.builder()
                .fileId(1)
                .privateFileId(100)
                .ownerId(1001)
                .fileName("test.txt")
                .fileData(testData)
                .build();

        String toStringResult = publicShare.toString();

        assertTrue(toStringResult.contains("fileId=1"));
        assertTrue(toStringResult.contains("privateFileId=100"));
        assertTrue(toStringResult.contains("ownerId=1001"));
        assertTrue(toStringResult.contains("fileName=test.txt"));
        assertTrue(toStringResult.contains("fileData"));
    }

    @Test
    void testPublicShareWithNullFileData() {
        PublicShare publicShare = PublicShare.builder()
                .fileId(1)
                .privateFileId(100)
                .ownerId(1001)
                .fileName("empty.txt")
                .fileData(null)
                .build();

        assertNotNull(publicShare);
        assertEquals(1, publicShare.getFileId());
        assertEquals(100, publicShare.getPrivateFileId());
        assertEquals(1001, publicShare.getOwnerId());
        assertEquals("empty.txt", publicShare.getFileName());
        assertNull(publicShare.getFileData());
    }
}