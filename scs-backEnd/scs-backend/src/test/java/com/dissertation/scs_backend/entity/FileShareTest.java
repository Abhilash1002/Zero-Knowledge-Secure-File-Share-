package com.dissertation.scs_backend.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FileShareTest {

    @Test
    void testFileShareCreation() {
        FileShare fileShare = FileShare.builder()
                .shareId(1)
                .fileId(100)
                .sender(1001)
                .receiver(1002)
                .key("encryptionKey")
                .iv("initializationVector")
                .expiry("2023-12-31")
                .build();

        assertNotNull(fileShare);
        assertEquals(1, fileShare.getShareId());
        assertEquals(100, fileShare.getFileId());
        assertEquals(1001, fileShare.getSender());
        assertEquals(1002, fileShare.getReceiver());
        assertEquals("encryptionKey", fileShare.getKey());
        assertEquals("initializationVector", fileShare.getIv());
        assertEquals("2023-12-31", fileShare.getExpiry());
    }

    @Test
    void testFileShareSettersAndGetters() {
        FileShare fileShare = new FileShare();

        fileShare.setShareId(2);
        fileShare.setFileId(200);
        fileShare.setSender(2001);
        fileShare.setReceiver(2002);
        fileShare.setKey("newEncryptionKey");
        fileShare.setIv("newInitializationVector");
        fileShare.setExpiry("2024-06-30");

        assertEquals(2, fileShare.getShareId());
        assertEquals(200, fileShare.getFileId());
        assertEquals(2001, fileShare.getSender());
        assertEquals(2002, fileShare.getReceiver());
        assertEquals("newEncryptionKey", fileShare.getKey());
        assertEquals("newInitializationVector", fileShare.getIv());
        assertEquals("2024-06-30", fileShare.getExpiry());
    }

    @Test
    void testFileShareEquality() {
        FileShare fileShare1 = FileShare.builder()
                .shareId(1)
                .fileId(100)
                .sender(1001)
                .receiver(1002)
                .key("encryptionKey")
                .iv("initializationVector")
                .expiry("2023-12-31")
                .build();

        FileShare fileShare2 = FileShare.builder()
                .shareId(1)
                .fileId(100)
                .sender(1001)
                .receiver(1002)
                .key("encryptionKey")
                .iv("initializationVector")
                .expiry("2023-12-31")
                .build();

        assertEquals(fileShare1, fileShare2);
        assertEquals(fileShare1.hashCode(), fileShare2.hashCode());
    }

    @Test
    void testFileShareToString() {
        FileShare fileShare = FileShare.builder()
                .shareId(1)
                .fileId(100)
                .sender(1001)
                .receiver(1002)
                .key("encryptionKey")
                .iv("initializationVector")
                .expiry("2023-12-31")
                .build();

        String toStringResult = fileShare.toString();

        assertTrue(toStringResult.contains("shareId=1"));
        assertTrue(toStringResult.contains("fileId=100"));
        assertTrue(toStringResult.contains("sender=1001"));
        assertTrue(toStringResult.contains("receiver=1002"));
        assertTrue(toStringResult.contains("key=encryptionKey"));
        assertTrue(toStringResult.contains("iv=initializationVector"));
        assertTrue(toStringResult.contains("expiry=2023-12-31"));
    }
}