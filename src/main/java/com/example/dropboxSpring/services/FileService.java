package com.example.dropboxSpring.services;

import com.example.dropboxSpring.dtos.UploadFileDto;
import com.example.dropboxSpring.exceptions.FileDoesNotExistException;
import com.example.dropboxSpring.exceptions.FolderDoesNotExistException;
import com.example.dropboxSpring.exceptions.UserDoesNotMatchOwnerOfFolderException;
import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FileRepository;
import com.example.dropboxSpring.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileService {

    private FileRepository fileRepository;
    private FolderRepository folderRepository;
    private UserService userService;

    @Autowired
    public FileService(FileRepository fileRepository, FolderRepository folderRepository, UserService userService) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.userService = userService;
    }

    @Transactional
    public UploadFileDto uploadFile(UUID folderId, MultipartFile file, String token) throws IOException {

        String name = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        File newFile = new File(name, file.getContentType(), file.getBytes());
        User user = userService.findUserByToken(token);

        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExistException("Folder with id " + folderId + " does not exist"));
        // This is for checking if the user of the folder is the same as the active token user.
        if (!checkFolderAuthentication(folder, user)) {
            throw new UserDoesNotMatchOwnerOfFolderException(
                    "The currently logged in user " + user.getEmail() +
                            " does not match the owner of this folder: " +
                             folder.getUser().getEmail());
        }

        fileRepository.save(newFile);
        folder.getFiles().add(newFile);
        folderRepository.save(folder);
        return new UploadFileDto(
                newFile.getId(),
                newFile.getName(),
                newFile.getType(),
               new String(newFile.getData(), StandardCharsets.UTF_8)
        );
    }

    public File getFileById(UUID folderId, UUID fileId, String token) {
        User user = userService.findUserByToken(token);
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExistException("Folder with id " + folderId + " does not exist"));
        if (!checkFolderAuthentication(folder, user)) {
            throw new UserDoesNotMatchOwnerOfFolderException(
                    "The currently logged in user " + user.getEmail() +
                            " does not match the owner of this folder: " +
                            folder.getUser().getEmail());
        }
        return fileRepository.findById(fileId).orElseThrow(
                () -> new FileDoesNotExistException("File with id" + fileId + " does not exist"));
    }

    public Stream<File> getAllFilesByFolder(UUID folderId, String token) {
        User user = userService.findUserByToken(token);
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExistException("Folder with id " + folderId + " does not exist"));

        if(!checkFolderAuthentication(folder, user)) {
            throw new UserDoesNotMatchOwnerOfFolderException(
                    "The currently logged in user " + user.getEmail() +
                            " does not match the owner of this folder: " +
                            folder.getUser().getEmail());
        }
        return folder.getFiles().stream();
    }


    public void deleteFileById(UUID fileId, String token) {
        User user = userService.findUserByToken(token);
        Folder folder = folderRepository.findFolderByFileId(fileId).orElseThrow(
                () -> new FolderDoesNotExistException("Cannot find folder with file id : " + fileId));

        if(!checkFolderAuthentication(folder, user)) {
            throw new UserDoesNotMatchOwnerOfFolderException(
                    "The currently logged in user " + user.getEmail() +
                            " does not match the owner of this folder: " +
                            folder.getUser().getEmail());
        }
        folder.getFiles().remove(fileRepository.findById(fileId).orElseThrow());
        folderRepository.save(folder);
            fileRepository.deleteById(fileId);

    }

    //deletes many files by adding them the file id to a string array
    public void deleteManyFilesById(UUID folderId, String token, List<String> listOfIds){
        User user = userService.findUserByToken(token);
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExistException("Cannot find folder with file id : " + folderId));

        if(!checkFolderAuthentication(folder, user)) {
            throw new UserDoesNotMatchOwnerOfFolderException(
                    "The currently logged in user " + user.getEmail() +
                            " does not match the owner of this folder: " +
                            folder.getUser().getEmail());
        }

        for(var id : listOfIds){
            folder.getFiles().remove(fileRepository.findById(UUID.fromString(id)).orElseThrow());
            folderRepository.save(folder);
            fileRepository.deleteById(UUID.fromString(id));
        }
    }

    // This is for checking if the user of the folder is the same as the active token user.
    public boolean checkFolderAuthentication(Folder folder, User activeUser){
        return (folder.getUser().getId() == activeUser.getId());
    }
}
