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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
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
        final MockMultipartFile mockFile = new MockMultipartFile("data", "test.txt", "text/plain", "hello test!".getBytes());
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

        var fileName = StringUtils.cleanPath(Objects.requireNonNull(mockFile.getOriginalFilename())) ;
        var type = mockFile.getContentType();
        var data = mockFile.getBytes();
        var newFile = new File(fileName, type, data);


        Mockito.when(folderRepository.findById(folder.getId())).thenReturn(Optional.of(folder));
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.save(newFile)).then(invocation -> {
            var file = (File) invocation.getArgument(0);
            file.setId(UUID.randomUUID());
            return file;
        });
        //when
        var result = Assertions.assertDoesNotThrow(() ->
                fileService.uploadFile(folder.getId(), mockFile, token));


        //then
        Assertions.assertEquals("test.txt", result.getName());
        Assertions.assertEquals("text/plain", result.getType());
        Assertions.assertEquals("hello test!", result.getData());
        Assertions.assertNotNull(result.getId());


    }
}
