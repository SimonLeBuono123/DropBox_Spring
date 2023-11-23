package com.example.dropboxSpring.services;

import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class FileService {

    private FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File createFile(UUID folderId, MultipartFile file) throws IOException {
        String name = StringUtils.cleanPath(file.getOriginalFilename());
        File File = new File(name, file.getContentType(), file.getBytes());

        return fileRepository.save(File);
    }

    public File getFile(UUID id){
        return fileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with " + id + " id does not exist"));
    }

}
