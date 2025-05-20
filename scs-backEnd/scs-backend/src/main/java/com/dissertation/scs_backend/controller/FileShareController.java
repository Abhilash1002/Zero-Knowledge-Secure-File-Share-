package com.dissertation.scs_backend.controller;

import com.dissertation.scs_backend.entity.PublicShare;
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
import com.dissertation.scs_backend.service.FileShareService;

import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Data
@RequestMapping("/api/v1")
public class FileShareController {

    private final FileShareService fileShareService;

    @PostMapping("/share")
    public ResponseEntity<FileShareResponse> shareFile(
            @RequestBody FileShareRequest request) {
        try {
            return ResponseEntity.ok().body(fileShareService.shareFile(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<List<FileShareListResponse>> getReceivedFiles(@RequestParam String email) {
        try {
            List<FileShareListResponse> receivedFilesList = fileShareService.getFilesReceivedByUser(email);
            return ResponseEntity.ok(receivedFilesList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/shared-users-by-file")
    public ResponseEntity<List<FileShareInfo>> getUsersListByFile(@RequestParam Integer fileId) {
        try {
            List<FileShareInfo> sharedWithList = fileShareService.getSharedUserListByFileId(fileId);
            return ResponseEntity.ok().body(sharedWithList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // not needed
    @GetMapping("/shared-by-me")
    public ResponseEntity<List<FileShareListResponse>> getSentFiles(@RequestParam String email) {
        try {
            List<FileShareListResponse> sentFilesList = fileShareService.getFilesSentByUser(email);
            return ResponseEntity.ok(sentFilesList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/shared-file-data")
    public ResponseEntity<SharedFileContentResponse> getSharedFileData(@RequestParam Integer shareId) {
        try {
            SharedFileContentResponse sharedFile = fileShareService.getSharedFileContents(shareId);
            return ResponseEntity.ok(sharedFile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-receiver-details")
    public ResponseEntity<ReceiverDetailsResponse> getReceiverDetails(@RequestParam String email) {
        try{
            ReceiverDetailsResponse receiverDetailsResponse = fileShareService.sendReceiverDetails(email);
            return ResponseEntity.ok(receiverDetailsResponse);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/revoke-file-access")
    public ResponseEntity<String> deleteFile(@RequestBody RevokeRequest request ){
        try{
            fileShareService.deleteFileContents(request);
            return ResponseEntity.ok("File Revoke Successful");
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-public-files-list")
    public ResponseEntity<List<PublicFileListResponse>> getPublicFileList() {
        try{
            List<PublicFileListResponse> publicFileList = fileShareService.getPublicFileList();
            return ResponseEntity.ok(publicFileList);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-sent-public-files-by-owner")
    public ResponseEntity<List<PublicFileListResponse>> getPublicFileList(@RequestParam String email) {
        try{
            List<PublicFileListResponse> publicFileList = fileShareService.getPublicFileListByOwner(email);
            return ResponseEntity.ok(publicFileList);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-public-file")
    public ResponseEntity<PublicFileContentResponse> getPublicFileContents(@RequestParam Integer fileId){
        try{
            PublicFileContentResponse publicFileList = fileShareService.getPublicFileContents(fileId);
            return ResponseEntity.ok(publicFileList);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/share-file-publicly")
    public ResponseEntity<PublicShare> shareFileToPublic(
            @RequestBody PublicFileShareRequest request) {
        try {
            var data = fileShareService.shareFileToPublic(request);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/is-public")
    public ResponseEntity<PublicShare> checkIfFileIsPublic(@RequestParam Integer fileId) {
        try {
            var data = fileShareService.isFilePublic(fileId);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/remove-public-file")
    public ResponseEntity<String> deleteFile(@RequestBody DeletePublicFileRequest request ){
        try{
            fileShareService.deletePublicFile(request);
            return ResponseEntity.ok("File removed from public");
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

}