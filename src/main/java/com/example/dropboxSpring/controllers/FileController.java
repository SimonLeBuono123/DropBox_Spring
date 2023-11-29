package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.FileDto;
import com.example.dropboxSpring.dtos.MessageDto;
import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//class for handling files http routes.
@RequestMapping("/file")
@RestController
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Http post method for uploading a file inside a folder.
    @PostMapping("/upload/folder/{folderId}")
    public ResponseEntity<MessageDto> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @PathVariable String folderId
    ) {
        String message = "";
        String reTokened = token.split(" ")[1].trim();
        try {

            fileService.uploadFile(UUID.fromString(folderId), file, reTokened);
            message = "Uploaded file successfully: " + file.getOriginalFilename();
            return ResponseEntity.ok(new MessageDto(message));
        } catch (Exception e) {
            message = String.valueOf(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }
    }

    //http get method for getting all the files of a certain folder.
    @GetMapping("/all/folder/{folderId}")
    public ResponseEntity<List<FileDto>> getAllFilesByFolder(
            @RequestHeader("Authorization") String token,
            @PathVariable String folderId
    ) {

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

    // Http method for getting and downloading a certain file by id.
    @GetMapping("/{fileId}/folder/{folderId}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @RequestHeader("Authorization") String token,
            @PathVariable String fileId,
            @PathVariable String folderId
    ) {
        String reTokened = token.split(" ")[1].trim();

        File file = fileService.getFileById(
                UUID.fromString(folderId),
                UUID.fromString(fileId)
                , reTokened);

        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename = \"" + file.getName() + "\"")
                .body(new ByteArrayResource(file.getData()));
    }

    //Http method for deleting a file.
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<MessageDto> deleteFileById(
            @RequestHeader("Authorization") String token,
            @PathVariable String fileId
    ) {
        String reTokened = token.split(" ")[1].trim();
        String message = "";
        try {
            fileService.deleteFileById(UUID.fromString(fileId), reTokened);
            message = "File successfully deleted!";
            return ResponseEntity.ok(new MessageDto(message));
        } catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }
    }

    @DeleteMapping("/delete/many/{folderId}")
    public ResponseEntity<MessageDto> deleteManyFilesByFolder(
            @RequestHeader("Authorization") String token,
            @PathVariable String folderId,
            @RequestBody List<String> listOfFileIds
    ){
        String reTokened = token.split(" ")[1].trim();
        String message = "";
        try {
            fileService.deleteManyFilesById(UUID.fromString(folderId), reTokened, listOfFileIds);
            message = "Files successfully deleted";
            return ResponseEntity.ok(new MessageDto(message));
        }catch (Exception e){
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }


    }
}
