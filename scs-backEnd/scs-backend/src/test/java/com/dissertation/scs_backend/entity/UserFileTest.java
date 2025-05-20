package com.dissertation.scs_backend.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

class UserFileTest {

    @Test
    void testUserFileCreation() {
        byte[] testData = "Test file content".getBytes();
        UserFile userFile = UserFile.builder()
                .fileId(1)
                .ownerId(100)
                .key("encryptionKey")
                .iv("initializationVector")
                .fileName("test.txt")
                .fileData(testData)
                .build();

        assertNotNull(userFile);
        assertEquals(1, userFile.getFileId());
        assertEquals(100, userFile.getOwnerId());
        assertEquals("encryptionKey", userFile.getKey());
        assertEquals("initializationVector", userFile.getIv());
        assertEquals("test.txt", userFile.getFileName());
        assertTrue(Arrays.equals(testData, userFile.getFileData()));
    }

    @Test
    void testUserFileSettersAndGetters() {
        UserFile userFile = new UserFile();
        byte[] testData = "Updated file content".getBytes();

        userFile.setFileId(2);
        userFile.setOwnerId(200);
        userFile.setKey("newEncryptionKey");
        userFile.setIv("newInitializationVector");
        userFile.setFileName("updated.txt");
        userFile.setFileData(testData);

        assertEquals(2, userFile.getFileId());
        assertEquals(200, userFile.getOwnerId());
        assertEquals("newEncryptionKey", userFile.getKey());
        assertEquals("newInitializationVector", userFile.getIv());
        assertEquals("updated.txt", userFile.getFileName());
        assertTrue(Arrays.equals(testData, userFile.getFileData()));
    }

    @Test
    void testUserFileEquality() {
        byte[] testData = "Test file content".getBytes();
        UserFile userFile1 = UserFile.builder()
                .fileId(1)
                .ownerId(100)
                .key("encryptionKey")
                .iv("initializationVector")
                .fileName("test.txt")
                .fileData(testData)
                .build();

        UserFile userFile2 = UserFile.builder()
                .fileId(1)
                .ownerId(100)
                .key("encryptionKey")
                .iv("initializationVector")
                .fileName("test.txt")
                .fileData(testData)
                .build();

        assertEquals(userFile1, userFile2);
        assertEquals(userFile1.hashCode(), userFile2.hashCode());
    }

    @Test
    void testUserFileToString() {
        byte[] testData = "Test file content".getBytes();
        UserFile userFile = UserFile.builder()
                .fileId(1)
                .ownerId(100)
                .key("encryptionKey")
                .iv("initializationVector")
                .fileName("test.txt")
                .fileData(testData)
                .build();

        String toStringResult = userFile.toString();

        assertTrue(toStringResult.contains("fileId=1"));
        assertTrue(toStringResult.contains("ownerId=100"));
        assertTrue(toStringResult.contains("key=encryptionKey"));
        assertTrue(toStringResult.contains("iv=initializationVector"));
        assertTrue(toStringResult.contains("fileName=test.txt"));
        assertTrue(toStringResult.contains("fileData"));
    }

    @Test
    void testUserFileWithNullFileData() {
        UserFile userFile = UserFile.builder()
                .fileId(1)
                .ownerId(100)
                .key("encryptionKey")
                .iv("initializationVector")
                .fileName("empty.txt")
                .fileData(null)
                .build();

        assertNotNull(userFile);
        assertEquals(1, userFile.getFileId());
        assertEquals(100, userFile.getOwnerId());
        assertEquals("encryptionKey", userFile.getKey());
        assertEquals("initializationVector", userFile.getIv());
        assertEquals("empty.txt", userFile.getFileName());
        assertNull(userFile.getFileData());
    }

    @Test
    void testUserFileWithLargeFileData() {
        byte[] largeTestData = new byte[1024 * 1024]; // 1 MB of data
        Arrays.fill(largeTestData, (byte) 'A');

        UserFile userFile = UserFile.builder()
                .fileId(1)
                .ownerId(100)
                .key("encryptionKey")
                .iv("initializationVector")
                .fileName("large_file.txt")
                .fileData(largeTestData)
                .build();

        assertNotNull(userFile);
        assertEquals(1, userFile.getFileId());
        assertEquals(100, userFile.getOwnerId());
        assertEquals("encryptionKey", userFile.getKey());
        assertEquals("initializationVector", userFile.getIv());
        assertEquals("large_file.txt", userFile.getFileName());
        assertEquals(1024 * 1024, userFile.getFileData().length);
        assertTrue(Arrays.equals(largeTestData, userFile.getFileData()));
    }
}