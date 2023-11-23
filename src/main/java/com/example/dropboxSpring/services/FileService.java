package com.example.dropboxSpring.services;

import com.example.dropboxSpring.models.FileDb;
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

    public FileDb createFile(MultipartFile file) throws IOException {
        String name = StringUtils.cleanPath(file.getOriginalFilename());
        FileDb fileDb = new FileDb(name, file.getContentType(), file.getBytes());

        return fileRepository.save(fileDb);
    }

    public FileDb getFile(UUID id){
        return fileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with " + id + " id does not exist"));
    }

}
