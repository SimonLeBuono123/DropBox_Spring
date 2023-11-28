package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.FileDto;
import com.example.dropboxSpring.dtos.MessageDto;
import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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


    @GetMapping("/all/folder/{folderId}")
    public ResponseEntity<List<FileDto>> getAllFilesByFolder(
            @RequestHeader("Authorization") String token,
            @PathVariable String folderId
    ){
        String reTokened = token.split(" ")[1].trim();
            List<FileDto> files = fileService
                    .getAllFilesByFolder(UUID.fromString(folderId), reTokened)
                    .map(file -> {
                        String downloadUri = ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/file/")
                                .path(file.getId() + "/")
                                .path("/folder/")
                                .path(folderId)
                                .toUriString();
                        return new FileDto(
                                file.getName(),
                                downloadUri,
                                file.getType(),
                                file.getData().length
                        );
                    }).collect(Collectors.toList());
            return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileId}/folder/{folderId}")
    public ResponseEntity<byte[]> getFileById(
            @RequestHeader("Authorization") String token,
            @PathVariable String fileId,
            @PathVariable String folderId
    ){
        String reTokened = token.split(" ")[1].trim();
        File file = fileService.getFileById(
                UUID.fromString(folderId),
                UUID.fromString(fileId)
                , reTokened);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename = \"" + file.getName() + "\"")
                .body(file.getData());
    }
    
}
