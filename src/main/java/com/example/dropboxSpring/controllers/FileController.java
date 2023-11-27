package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.MessageDto;
import com.example.dropboxSpring.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping("/file")
@RestController
public class FileController {

    private FileService fileService;
    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @PostMapping("/upload/folder/{folderId}")
    public ResponseEntity<MessageDto> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file")MultipartFile file,
            @PathVariable String folderId
            ){
         String message = "";
         String reTokened = token.split(" ")[1].trim();
        try {

            fileService.uploadFile(UUID.fromString(folderId), file, reTokened);
                    message = "Uploaded file successfully: " + file.getOriginalFilename();
            return ResponseEntity.ok(new MessageDto(message));
        }catch (Exception e){
            message = String.valueOf(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }
    }
}
