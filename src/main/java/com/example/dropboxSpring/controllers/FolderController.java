package com.example.dropboxSpring.controllers;


import com.example.dropboxSpring.dtos.CreateFolderDto;
import com.example.dropboxSpring.exceptions.FolderNameOfUserAlreadyExistsException;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.services.FolderService;
import com.example.dropboxSpring.utils.TokenStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class for handling different http methods and routes
 * for the model entity folder
 */
@RestController
@RequestMapping("/folder")
public class FolderController {

    private FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }


    /**
     * Http post method for creating folders
     * @param token
     * @param folderDto
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> createFolder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateFolderDto folderDto
            ) throws FolderNameOfUserAlreadyExistsException {
        //For removing empty space and the first bearer that gets added when getting token from authorization and returns
        String reTokened = TokenStringUtils.removeEmptySpace(token);
        try {
            return ResponseEntity.ok(folderService.createFolder(reTokened, folderDto));
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    /**
     * Http get method for finding all folders of logged in user.
     * @param token
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<List<Folder>> getAllFolderByUser(
            @RequestHeader("Authorization") String token
    ){
        String reTokened = TokenStringUtils.removeEmptySpace(token);
        return ResponseEntity.ok(folderService.findAllFoldersOfUser(reTokened));
    }
}
