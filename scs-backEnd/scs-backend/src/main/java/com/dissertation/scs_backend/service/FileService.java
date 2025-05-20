package com.dissertation.scs_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.dissertation.scs_backend.model.FileContentResponse;
import com.dissertation.scs_backend.model.FileDeleteRequest;
import com.dissertation.scs_backend.model.FileListRequest;
import com.dissertation.scs_backend.repository.FileShareRepository;
import com.dissertation.scs_backend.repository.PublicShareRepository;
import com.dissertation.scs_backend.repository.UserFileRepository;
import com.dissertation.scs_backend.repository.UserRepository;
import com.dissertation.scs_backend.entity.FileShare;
import com.dissertation.scs_backend.entity.PublicShare;
import com.dissertation.scs_backend.entity.UserFile;
import com.dissertation.scs_backend.model.FileListResponse;
import com.dissertation.scs_backend.model.FileUploadRequest;

import lombok.Data;

@Service
@Data
public class FileService {

    private final UserRepository userRepository;
    private final UserFileRepository fileRepository;
    private final FileShareRepository fileShareRepository;
    private final PublicShareRepository publicShareRepository;

    Logger logger = Logger.getLogger(getClass().getName());

    public void uploadFile(FileUploadRequest request) {
        UserFile fileEntity = new UserFile();
        
        var userData = userRepository.findByEmail(request.getOwnerEmail()).orElseThrow();
        var ownerId = userData.getId();
        fileEntity.setOwnerId(ownerId);
        fileEntity.setKey(request.getKey());
        fileEntity.setIv(request.getIv());
        fileEntity.setFileName(request.getFileName());
        fileEntity.setFileData(request.getFileData());

        fileRepository.save(fileEntity);
    }

    public List<FileListResponse> getFileDetailsByEmail(FileListRequest request) {
        var userData = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var ownerId = userData.getId();
        List<UserFile> userFiles = fileRepository.findByOwnerId(ownerId);

        return userFiles.stream()
                .map(file -> new FileListResponse(file.getFileId(), file.getFileName(), file.getKey(), file.getIv()))
                .collect(Collectors.toList());
    }

    public List<FileListResponse> getFileDetailsByEmail(String email) {
        var userData = userRepository.findByEmail(email).orElseThrow();
        var ownerId = userData.getId();
        List<UserFile> userFiles = fileRepository.findByOwnerId(ownerId);

        return userFiles.stream()
                .map(file -> new FileListResponse(file.getFileId(), file.getFileName(), file.getKey(), file.getIv()))
                .collect(Collectors.toList());
    }

    public FileContentResponse getFileContents(Integer fileId){
        var file = fileRepository.findById(fileId).orElseThrow();
        return FileContentResponse.builder()
                .fileName(file.getFileName())
                .key(file.getKey())
                .iv(file.getIv())
                .fileData(file.getFileData())
                .build();
    }

    public void deleteFileContents(FileDeleteRequest request){
        var file = fileRepository.findById(request.getFileId()).orElseThrow();
        List<FileShare> shareList = fileShareRepository.findAllByFileId(request.getFileId());
        Optional<PublicShare> publicFile = publicShareRepository.findByPrivateFileId(request.getFileId());
        try{
            fileRepository.delete(file);
            shareList.forEach(fileShareRepository::delete);
            publicFile.ifPresent(publicShare -> publicShareRepository.delete(publicShare));
            logger.info("File with id: " + request.getFileId() + " deleted successfully");
        }
        catch(Exception e){
            logger.warning("Error deleting the file");
        }
    }
}

