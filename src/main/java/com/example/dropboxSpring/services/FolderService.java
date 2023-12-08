package com.example.dropboxSpring.services;

import com.example.dropboxSpring.dtos.CreateFolderDto;
import com.example.dropboxSpring.exceptions.FolderNameOfUserAlreadyExistsException;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class for providing business functionality to entity model Folder.
 */

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;

    /**
     * Method for creating a folder by given logged-in user
     *
     * @param token
     * @param dto
     * @return
     */
    public Folder createFolder(String token, CreateFolderDto dto)
            throws FolderNameOfUserAlreadyExistsException {

        User user = userService.findUserByToken(token);

        var folder = Folder.builder()
                .name(dto.getName())
                .user(user)
                .build();

        List<Folder> foldersByUser = folderRepository.findAllFoldersByUserId(user.getId());

        if (containsName(foldersByUser, folder.getName())) {
            throw new FolderNameOfUserAlreadyExistsException
                    ("Folder of name " + folder.getName() + " already exists");
        }

        return folderRepository.save(folder);
    }

    /**
     * Boolean method for checking if a list of folders contains the same name of folder
     *
     * @param folders
     * @param folderName
     * @return
     */
    private boolean containsName(List<Folder> folders, String folderName) {
        return folders.stream().map(Folder::getName).anyMatch(folderName::equals);
    }
}
