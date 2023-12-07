package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.FileDto;
import com.example.dropboxSpring.dtos.MessageDto;
import com.example.dropboxSpring.dtos.UploadFileDto;
import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.repositories.FileRepository;
import com.example.dropboxSpring.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class for handling different http methods and routes
 * for the model entity file
 */
@RequestMapping("/file")
@RestController
public class FileController {

    private FileService fileService;
    private FileRepository fileRepository;
    @Autowired
    public FileController(FileService fileService, FileRepository fileRepository) {
        this.fileService = fileService;
        this.fileRepository = fileRepository;
    }

    /**
     * Http post method for uploading file to a folder
     * Uses a generic wildcard, so I can return both error message and file value
     * depending on final result
     * @param token
     * @param file
     * @param folderId
     * @return
     */
    /* Http post method for uploading a file to folder.

     */
    @PostMapping("/upload/folder/{folderId}")
    public ResponseEntity<?> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @PathVariable String folderId
    ) {
        String message = "";
        String reTokened = token.split(" ")[1].trim();
        try {

            UploadFileDto result = fileService.uploadFile(UUID.fromString(folderId), file, reTokened);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            message = String.valueOf(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }
    }

    /**
     * Http get method for getting all the files of a given folder
     * The fileDto class also has a variabel than return an url of the given file
     * which also includes a download.
     * @param token
     * @param folderId
     * @return
     */
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


    /**
     * Http method for getting and downloading a chosen file by id.
     * @param token
     * @param fileId
     * @param folderId
     * @return
     */
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

    /**
     * Http method for deleting one file by id.
     * This method unlike the others does not take in the folder id as a pathvariabel
     * because it searches for the folder by the file id.
     * I did this because I wanted to try a different approach on getting a folder with help
     * of a one-to-many table for folder and files.
     * @param token
     * @param fileId
     * @return
     */
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


    /**
     * Http delete method for deleting many files of given folder.
     * @param token
     * @param folderId
     * @param listOfFileIds
     * @return
     */
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
            return ResponseEntity.ok(new MessageDto(message, fileRepository.findAll()));
        }catch (Exception e){
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }


    }
}
