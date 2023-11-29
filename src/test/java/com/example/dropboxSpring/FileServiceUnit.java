package com.example.dropboxSpring;

import com.example.dropboxSpring.models.File;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FileRepository;
import com.example.dropboxSpring.repositories.FolderRepository;
import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.security.JwtUtility;
import com.example.dropboxSpring.services.FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application-test.properties")
public class FileServiceUnit {

    @Autowired
    FileService fileService;

    @Autowired
    JwtUtility jwtUtility;


    @MockBean
    FileRepository fileRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    FolderRepository folderRepository;


    @Test
    void uploadFile() throws IOException {
        //given
        final MockMultipartFile file = new MockMultipartFile("data", "test.txt", "text/plain", "hello test!".getBytes());
        User user = User.builder().id(UUID.randomUUID())
                .email("test@email.com")
                .password("password123")
                .name("test")
                .authorities(Arrays.asList("ROLE_USER"))
                .build();


        Folder folder = Folder.builder()
                .id(UUID.randomUUID())
                .files(new ArrayList<>())
                .name("tester")
                .user(user)
                .build();

        String token = jwtUtility.generateToken(user.getEmail(), user.getAuthorities());

        var originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())) ;
        var type = file.getContentType();
        var data = file.getBytes();
        var newFile = new File(originalFileName, type, data);


        Mockito.when(folderRepository.findById(folder.getId())).thenReturn(Optional.of(folder));
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.save(newFile)).then(invocation -> {
            var fileMock = (File) invocation.getArgument(0);
            fileMock.setId(UUID.randomUUID());
            return fileMock;
        });
        //when
        var result = Assertions.assertDoesNotThrow(() -> fileService.uploadFile(folder.getId(), file, token));


        //then
        Assertions.assertEquals("test.txt", result.getName());
        Assertions.assertEquals("text/plain", result.getType());
        Assertions.assertEquals("hello test!", new String(result.getData(), StandardCharsets.UTF_8));
        Assertions.assertNotNull(result.getId());


    }
}
