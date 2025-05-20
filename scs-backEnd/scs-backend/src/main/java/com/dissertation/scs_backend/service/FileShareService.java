package com.dissertation.scs_backend.service;

import com.dissertation.scs_backend.entity.FileShare;
import com.dissertation.scs_backend.entity.PublicShare;
import com.dissertation.scs_backend.entity.User;
import com.dissertation.scs_backend.exception.UserFileNotFoundException;
import com.dissertation.scs_backend.model.RevokeRequest;
import com.dissertation.scs_backend.model.sharing.DeletePublicFileRequest;
import com.dissertation.scs_backend.model.sharing.FileShareInfo;
import com.dissertation.scs_backend.model.sharing.FileShareListResponse;
import com.dissertation.scs_backend.model.sharing.FileShareRequest;
import com.dissertation.scs_backend.model.sharing.FileShareResponse;
import com.dissertation.scs_backend.model.sharing.PublicFileContentResponse;
import com.dissertation.scs_backend.model.sharing.PublicFileListResponse;
import com.dissertation.scs_backend.model.sharing.PublicFileShareRequest;
import com.dissertation.scs_backend.model.sharing.ReceiverDetailsResponse;
import com.dissertation.scs_backend.model.sharing.SharedFileContentResponse;
import com.dissertation.scs_backend.repository.FileShareRepository;
import com.dissertation.scs_backend.repository.PublicShareRepository;
import com.dissertation.scs_backend.repository.UserFileRepository;
import com.dissertation.scs_backend.repository.UserRepository;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Data
public class FileShareService {

    private final FileShareRepository fileShareRepository;
    private final UserFileRepository userFileRepository;
    private final UserRepository userRepository;
    private final PublicShareRepository publicShareRepository;

    Logger logger = Logger.getLogger(getClass().getName());

    public FileShareResponse shareFile(FileShareRequest request) {
        User sentBy = userRepository.findByEmail(request.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sharing user not found"));
        User sentTo = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Recipient user not found"));
        
        //check valid file
        userFileRepository.findById((int)request.getFileId())
                .orElseThrow(() -> new UserFileNotFoundException("File not found with id: " + request.getFileId()));

        FileShare fileShare = FileShare.builder()
            .fileId(request.getFileId())
            .sender(sentBy.getId())
            .receiver(sentTo.getId())
            .key(request.getKey())
            .iv(request.getIv())
            .expiry(request.getExpiry())
            .build();
        
        logger.info("Shared file with " + request.getReceiverEmail() + " file ID: "+ request.getFileId());

        fileShareRepository.save(fileShare);
        return FileShareResponse.builder()
            .fileId(fileShare.getFileId())
            .shareId(fileShare.getShareId())
            .senderEmail(request.getSenderEmail())
            .receiverEmail(request.getReceiverEmail())
            .expiry(fileShare.getExpiry())
            .build();
    }

    public List<FileShareInfo> getSharedUserListByFileId(Integer number) {
        List<FileShare> shareMappings = fileShareRepository.findAllByFileId(number);
        return shareMappings.stream().map(shareRow -> new FileShareInfo(
            shareRow.getShareId(),
            shareRow.getFileId(),
            userRepository.findById(shareRow.getReceiver()).orElseThrow().getUserTag(),
            userRepository.findById(shareRow.getReceiver()).orElseThrow().getEmail(),
            shareRow.getExpiry()
        )).collect(Collectors.toList());
    }

    public List<FileShareListResponse> getFilesSentByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var fileShareList = fileShareRepository.findBySender(user.getId());
        logger.info("userId :"+ user.getEmail());
        return fileShareList.stream()
                .map(shareRow -> new FileShareListResponse(shareRow.getShareId(), 
                                                            shareRow.getFileId(), 
                                                            userFileRepository.findByFileId(shareRow.getFileId()).getFileName(), 
                                                            userRepository.findById(shareRow.getSender()).orElseThrow().getEmail(), 
                                                            userRepository.findById(shareRow.getReceiver()).orElseThrow().getEmail(), 
                                                            shareRow.getExpiry()
                                                            )
                    ).collect(Collectors.toList());
    }

    public List<FileShareListResponse> getFilesReceivedByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var fileShareList = fileShareRepository.findByReceiver(user.getId());
        return fileShareList.stream()
                .map(shareRow -> new FileShareListResponse(shareRow.getShareId(), 
                                                            shareRow.getFileId(), 
                                                            userFileRepository.findByFileId(shareRow.getFileId()).getFileName(), 
                                                            userRepository.findById(shareRow.getSender()).orElseThrow().getEmail(), 
                                                            userRepository.findById(shareRow.getReceiver()).orElseThrow().getEmail(), 
                                                            shareRow.getExpiry()
                                                            )
                    ).collect(Collectors.toList());
    }

    public SharedFileContentResponse getSharedFileContents(Integer shareId){
        var sharedDetails = fileShareRepository.findByShareId(shareId).orElseThrow();

        var fileDetails = userFileRepository.findById(sharedDetails.getFileId()).orElseThrow();

        return SharedFileContentResponse.builder()
                    .fileName(fileDetails.getFileName())
                    .key(sharedDetails.getKey())
                    .iv(sharedDetails.getIv())
                    .fileData(fileDetails.getFileData())
                    .build();
        }

    public ReceiverDetailsResponse sendReceiverDetails(String email){
        var userDetails = userRepository.findByEmail(email).orElseThrow();
        return ReceiverDetailsResponse.builder()
                        .emailId(userDetails.getEmail())
                        .publicKey(userDetails.getPublicKey())
                        .userName(userDetails.getUsername())
                        .build();
    }

    public void deleteFileContents(RevokeRequest request){
        var shareRow = fileShareRepository.findById(request.getShareId()).orElseThrow();
        try{
            fileShareRepository.delete(shareRow);
            logger.info("Shared_data with id:" + request.getShareId() + " removed successfully");
        }
        catch(Exception e){
            logger.warning("Error deleting the file");
        }
    }

    public List<PublicFileListResponse> getPublicFileList(){
        List<PublicShare> publicRow = new ArrayList<>();
            publicShareRepository.findAll().forEach(publicRow::add);
        return publicRow.stream()
                .map(row -> new PublicFileListResponse(row.getFileId(),row.getFileName()))
                .collect(Collectors.toList());
    }

    public List<PublicFileListResponse> getPublicFileListByOwner(String email){

        var owner = userRepository.findByEmail(email).orElseThrow();
        List<PublicShare> publicRow = publicShareRepository.findByOwnerId(owner.getId());
        return publicRow.stream()
                .map(row -> new PublicFileListResponse(row.getFileId(),row.getFileName()))
                .collect(Collectors.toList());
    } 

    public PublicFileContentResponse getPublicFileContents(Integer fileId){
        PublicShare file = publicShareRepository.findByfileId(fileId).orElseThrow();
        User user = userRepository.findById(file.getOwnerId()).orElseThrow();
        return PublicFileContentResponse.builder()
                                .fileName(file.getFileName())
                                .fileData(file.getFileData())
                                .fileOwner(user.getUserTag())
                                .build();
    }

    public PublicShare shareFileToPublic(PublicFileShareRequest request){
        var userData = userRepository.findByEmail(request.getOwnerEmail()).orElseThrow();

        PublicShare fileEntity = PublicShare.builder()
            .fileId(0)
            .ownerId(userData.getId())
            .privateFileId(request.getFileId())
            .fileName(request.getFileName())
            .fileData(request.getFileData())
            .build();

        var saved = publicShareRepository.save(fileEntity);
        return saved;
    }

    public PublicShare isFilePublic(Integer fileId) {
        var data = publicShareRepository.findByPrivateFileId(fileId).orElse(null);
        return data;
    }

    public void deletePublicFile(DeletePublicFileRequest request){
        logger.info("Make private request "+request.toString());
        var shareRow = publicShareRepository.findById(request.getFileId()).orElseThrow();
        logger.info(shareRow.toString());
        try{
            publicShareRepository.delete(shareRow);
            logger.info("public File with id:" + request.getFileId() + " removed successfully");
        }
        catch(Exception e){
            logger.warning("Error deleting the file");
        }
    }

}