package com.dissertation.scs_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dissertation.scs_backend.entity.FileShare;
import com.dissertation.scs_backend.entity.PublicShare;
import com.dissertation.scs_backend.entity.User;
import com.dissertation.scs_backend.entity.UserFile;
import com.dissertation.scs_backend.model.FileContentResponse;
import com.dissertation.scs_backend.model.FileDeleteRequest;
import com.dissertation.scs_backend.model.FileListRequest;
import com.dissertation.scs_backend.model.FileListResponse;
import com.dissertation.scs_backend.model.FileUploadRequest;
import com.dissertation.scs_backend.repository.FileShareRepository;
import com.dissertation.scs_backend.repository.PublicShareRepository;
import com.dissertation.scs_backend.repository.UserFileRepository;
import com.dissertation.scs_backend.repository.UserRepository;

class FileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFileRepository fileRepository;

    @Mock
    private FileShareRepository fileShareRepository;

    @Mock
    private PublicShareRepository publicShareRepository;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile() {
        // Arrange
        FileUploadRequest request = new FileUploadRequest();
        request.setOwnerEmail("test@example.com");
        request.setKey("encryptionKey");
        request.setIv("initializationVector");
        request.setFileName("test.txt");
        request.setFileData("fileContent".getBytes());

        User user = new User();
        user.setId(1);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        fileService.uploadFile(request);

        // Assert
        verify(userRepository).findByEmail("test@example.com");
        verify(fileRepository).save(any(UserFile.class));
    }

    @Test
    void testGetFileDetailsByEmail() {
        // Arrange
        FileListRequest request = new FileListRequest();
        request.setEmail("test@example.com");

        User user = new User();
        user.setId(1);

        UserFile file1 = new UserFile();
        file1.setFileId(1);
        file1.setFileName("file1.txt");
        file1.setKey("key1");
        file1.setIv("iv1");

        UserFile file2 = new UserFile();
        file2.setFileId(2);
        file2.setFileName("file2.txt");
        file2.setKey("key2");
        file2.setIv("iv2");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(fileRepository.findByOwnerId(1)).thenReturn(Arrays.asList(file1, file2));

        // Act
        List<FileListResponse> result = fileService.getFileDetailsByEmail(request);

        // Assert
        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0).getFileName());
        assertEquals("file2.txt", result.get(1).getFileName());
        verify(userRepository).findByEmail("test@example.com");
        verify(fileRepository).findByOwnerId(1);
    }

    @Test
    void testGetFileContents() {
        // Arrange
        Integer fileId = 1;
        UserFile file = new UserFile();
        file.setFileId(fileId);
        file.setFileName("test.txt");
        file.setKey("encryptionKey");
        file.setIv("initializationVector");
        file.setFileData("fileContent".getBytes());

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        // Act
        FileContentResponse result = fileService.getFileContents(fileId);

        // Assert
        assertEquals("test.txt", result.getFileName());
        assertEquals("encryptionKey", result.getKey());
        assertEquals("initializationVector", result.getIv());
        assertArrayEquals("fileContent".getBytes(), result.getFileData());
        verify(fileRepository).findById(fileId);
    }

    @Test
    void testDeleteFileContents() {
        // Arrange
        FileDeleteRequest request = new FileDeleteRequest();
        request.setFileId(1);

        UserFile file = new UserFile();
        file.setFileId(1);

        FileShare fileShare = new FileShare();
        PublicShare publicShare = new PublicShare();

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));
        when(fileShareRepository.findAllByFileId(1)).thenReturn(Arrays.asList(fileShare));
        when(publicShareRepository.findByPrivateFileId(1)).thenReturn(Optional.of(publicShare));

        // Act
        fileService.deleteFileContents(request);

        // Assert
        verify(fileRepository).delete(file);
        verify(fileShareRepository).delete(fileShare);
        verify(publicShareRepository).delete(publicShare);
    }

    @Test
    void testGetFileDetailsByEmailString() {
        // Arrange
        String email = "test@example.com";

        User user = new User();
        user.setId(1);

        UserFile file1 = new UserFile();
        file1.setFileId(1);
        file1.setFileName("file1.txt");
        file1.setKey("key1");
        file1.setIv("iv1");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(fileRepository.findByOwnerId(1)).thenReturn(Arrays.asList(file1));

        // Act
        List<FileListResponse> result = fileService.getFileDetailsByEmail(email);

        // Assert
        assertEquals(1, result.size());
        assertEquals("file1.txt", result.get(0).getFileName());
        verify(userRepository).findByEmail(email);
        verify(fileRepository).findByOwnerId(1);
    }
}