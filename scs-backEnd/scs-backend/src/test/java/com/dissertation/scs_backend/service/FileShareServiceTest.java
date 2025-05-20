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
import com.dissertation.scs_backend.model.sharing.*;
import com.dissertation.scs_backend.repository.FileShareRepository;
import com.dissertation.scs_backend.repository.PublicShareRepository;
import com.dissertation.scs_backend.repository.UserFileRepository;
import com.dissertation.scs_backend.repository.UserRepository;

class FileShareServiceTest {

    @Mock
    private FileShareRepository fileShareRepository;
    @Mock
    private UserFileRepository userFileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PublicShareRepository publicShareRepository;

    @InjectMocks
    private FileShareService fileShareService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShareFile() {
        // Arrange
        FileShareRequest request = new FileShareRequest();
        request.setSenderEmail("sender@example.com");
        request.setReceiverEmail("receiver@example.com");
        request.setFileId(1);
        request.setKey("key");
        request.setIv("iv");
        request.setExpiry("2023-12-31");

        User sender = new User();
        sender.setId(1);
        User receiver = new User();
        receiver.setId(2);
        UserFile userFile = new UserFile();
        userFile.setFileId(1);
        FileShare fileShare = new FileShare();
        fileShare.setShareId(1);

        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(userFileRepository.findById(1)).thenReturn(Optional.of(userFile));
        when(fileShareRepository.save(any(FileShare.class))).thenReturn(fileShare);

        // Act
        FileShareResponse response = fileShareService.shareFile(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getFileId());
        // assertEquals(1, response.getShareId());
        assertEquals("sender@example.com", response.getSenderEmail());
        assertEquals("receiver@example.com", response.getReceiverEmail());
        assertEquals("2023-12-31", response.getExpiry());

        verify(fileShareRepository).save(any(FileShare.class));
    }

    @Test
    void testGetSharedUserListByFileId() {
        // Arrange
        Integer fileId = 1;
        FileShare fileShare = new FileShare();
        fileShare.setShareId(1);
        fileShare.setFileId(fileId);
        fileShare.setReceiver(2);
        fileShare.setExpiry("2023-12-31");

        User user = new User();
        user.setUserName("receiverName");
        user.setEmail("receiver@example.com");

        when(fileShareRepository.findAllByFileId(fileId)).thenReturn(Arrays.asList(fileShare));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));

        // Act
        List<FileShareInfo> result = fileShareService.getSharedUserListByFileId(fileId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getShareId());
        assertEquals(fileId, result.get(0).getFileId());
        assertEquals("2023-12-31", result.get(0).getExpiry());

        verify(fileShareRepository).findAllByFileId(fileId);
        verify(userRepository, times(2)).findById(2);
    }

    @Test
    void testGetFilesSentByUser() {
        // Arrange
        String email = "sender@example.com";
        User user = new User();
        user.setId(1);
        user.setEmail(email);

        FileShare fileShare = new FileShare();
        fileShare.setShareId(1);
        fileShare.setFileId(1);
        fileShare.setSender(1);
        fileShare.setReceiver(2);
        fileShare.setExpiry("2023-12-31");

        UserFile userFile = new UserFile();
        userFile.setFileName("test.txt");

        User sender = new User();
        sender.setEmail("sender@example.com");
        User receiver = new User();
        receiver.setEmail("receiver@example.com");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(fileShareRepository.findBySender(1)).thenReturn(Arrays.asList(fileShare));
        when(userFileRepository.findByFileId(1)).thenReturn(userFile);
        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));

        // Act
        List<FileShareListResponse> result = fileShareService.getFilesSentByUser(email);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getShareId());
        assertEquals(1, result.get(0).getFileId());
        assertEquals("test.txt", result.get(0).getFileName());
        assertEquals("2023-12-31", result.get(0).getExpiry());

        verify(userRepository).findByEmail(email);
        verify(fileShareRepository).findBySender(1);
        verify(userFileRepository).findByFileId(1);
        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
    }

    @Test
    void testGetSharedFileContents() {
        // Arrange
        Integer shareId = 1;
        FileShare fileShare = new FileShare();
        fileShare.setFileId(1);
        fileShare.setKey("key");
        fileShare.setIv("iv");

        UserFile userFile = new UserFile();
        userFile.setFileName("test.txt");
        userFile.setFileData("file content".getBytes());

        when(fileShareRepository.findByShareId(shareId)).thenReturn(Optional.of(fileShare));
        when(userFileRepository.findById(1)).thenReturn(Optional.of(userFile));

        // Act
        SharedFileContentResponse response = fileShareService.getSharedFileContents(shareId);

        // Assert
        assertEquals("test.txt", response.getFileName());
        assertEquals("key", response.getKey());
        assertEquals("iv", response.getIv());
        assertArrayEquals("file content".getBytes(), response.getFileData());

        verify(fileShareRepository).findByShareId(shareId);
        verify(userFileRepository).findById(1);
    }

    @Test
    void testSendReceiverDetails() {
        // Arrange
        String email = "receiver@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPublicKey("publicKey");
        user.setUserName("receiverName");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        ReceiverDetailsResponse response = fileShareService.sendReceiverDetails(email);

        // Assert
        assertEquals(email, response.getEmailId());
        assertEquals("publicKey", response.getPublicKey());
        assertEquals("receiver@example.com", response.getUserName());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void testShareFileToPublic() {
        // Arrange
        PublicFileShareRequest request = new PublicFileShareRequest();
        request.setOwnerEmail("owner@example.com");
        request.setFileId(1);
        request.setFileName("public.txt");
        request.setFileData("public content".getBytes());

        User user = new User();
        user.setId(1);

        PublicShare publicShare = new PublicShare();
        publicShare.setFileId(2);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(publicShareRepository.save(any(PublicShare.class))).thenReturn(publicShare);

        // Act
        PublicShare result = fileShareService.shareFileToPublic(request);

        // Assert
        assertEquals(2, result.getFileId());

        verify(userRepository).findByEmail("owner@example.com");
        verify(publicShareRepository).save(any(PublicShare.class));
    }

    @Test
    void testDeletePublicFile() {
        // Arrange
        DeletePublicFileRequest request = new DeletePublicFileRequest();
        request.setFileId(1);

        PublicShare publicShare = new PublicShare();
        publicShare.setFileId(1);

        when(publicShareRepository.findById(1)).thenReturn(Optional.of(publicShare));

        // Act
        fileShareService.deletePublicFile(request);

        // Assert
        verify(publicShareRepository).findById(1);
        verify(publicShareRepository).delete(publicShare);
    }
}