package com.dissertation.scs_backend.controller;

import com.dissertation.scs_backend.model.FileContentResponse;
import com.dissertation.scs_backend.model.FileListResponse;
import com.dissertation.scs_backend.model.FileUploadRequest;
import com.dissertation.scs_backend.model.FileDeleteRequest;
import com.dissertation.scs_backend.service.FileService;

import lombok.Data;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @PostMapping("/send-file")
    public ResponseEntity<String> uploadFile(@RequestBody FileUploadRequest request) {
        try {
            fileService.uploadFile(request);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/my-files")
    public ResponseEntity<List<FileListResponse>> getFileDetails(@RequestParam String email) {
        try {
            List<FileListResponse> fileDetails = fileService.getFileDetailsByEmail(email);
            return ResponseEntity.ok(fileDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-file")
    public ResponseEntity<FileContentResponse> getFileContents(@RequestParam Integer fileId){
        try{
            FileContentResponse fileContents = fileService.getFileContents(fileId);
            return ResponseEntity.ok(fileContents);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delete-file")
    public ResponseEntity<String> deleteFile(@RequestBody FileDeleteRequest request ){
        try{
            fileService.deleteFileContents(request);
            return ResponseEntity.ok("File Deleted Successfully");
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}