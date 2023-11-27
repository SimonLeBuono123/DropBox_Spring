package com.example.dropboxSpring.services;

import com.example.dropboxSpring.exceptions.FolderDoesNotExistException;
import com.example.dropboxSpring.exceptions.UserDoesNotMatchException;
import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FileRepository;
import com.example.dropboxSpring.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.UUID;

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

    public File uploadFile(UUID folderId, MultipartFile file, String token) throws IOException {

        String name = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        File file1 = new File(name, file.getContentType(), convert(file).toString().getBytes());
        User user = userService.findUserByToken(token);
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExistException("Folder with id " + folderId + " does not exist"));
        if(folder.getUser().getId() != user.getId()){
            throw new UserDoesNotMatchException(
                    "The currently logged in user " + user.getEmail() + " does not match the owner of this folder " +
                            "and can therefore not upload file in this folder");
        }
        folder.getFiles().add((com.example.dropboxSpring.models.File) file);
        folderRepository.save(folder);
        return fileRepository.save(file1);
    }

    public File getFile(UUID id){
        return fileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with " + id + " id does not exist"));
    }

    private static Path convert(MultipartFile file) throws IOException{
        Path newFile = Paths.get(Objects.requireNonNull(file.getOriginalFilename()));
        try(InputStream is = file.getInputStream();
            OutputStream os = Files.newOutputStream(newFile)){
            byte[] buffer = new byte[4096];
            int read = 0;
            while((read = is.read(buffer)) > 0){
                os.write(buffer, 0, read);
            }
        }
        return newFile;
    }

}
