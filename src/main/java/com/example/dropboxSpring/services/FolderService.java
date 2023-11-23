package com.example.dropboxSpring.services;

import com.example.dropboxSpring.dtos.CreateFolderDto;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FolderRepository;
import com.example.dropboxSpring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;

    public Folder createFolder(String token, CreateFolderDto dto) {
        User user = userService.findUserByToken(token);
        var folder = Folder.builder()
                .name(dto.getName())
                .user(user)
                .build();
        return folderRepository.save(folder);
    }

}
