package com.example.dropboxSpring;

import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FolderRepository;
import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.security.JwtUtility;
import com.example.dropboxSpring.services.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application-test.properties")
public class FileIntegrationTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    JwtUtility jwtUtility;

    @Autowired
    FileService fileService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FolderRepository folderRepository;

    @BeforeEach
    void beforeEach(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "folder_files","folder", "user", "files" );
    }

    /*this is a method for testing if a file and its content gets successfully uploaded
    */
    @Test
    void uploadNewFileSuccess() throws Exception {
        //given
        var user = User.builder()
                .email("test@test")
                .password("password123")
                .name("test")
                .authorities(Arrays.asList("ROLE_USER"))
                .build();

        // creates a user that will be the owner of folder
        userRepository.save(user);

        var folder = Folder.builder()
                .name("tests")
                .user(user)
                .build();

        //creates a folder that will hold the file
        folderRepository.save(folder);

        var fileName = "test.txt";
        var contentType = "text/plain";
        var data = "this is a test".getBytes();

        final MockMultipartFile file = new MockMultipartFile("file", fileName, contentType, data);

        // converts uuid to string
        String folderId = String.valueOf(folder.getId());

        //generate a token for created user
        var token = jwtUtility.generateToken(user.getEmail(), user.getAuthorities());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        //when

        var request = MockMvcRequestBuilders
                .multipart("/file/upload/folder/{folderId}", folderId)
                .file(file)
                .headers(headers)
                .param("file", String.valueOf(file))
                .content(file.getBytes())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept("*/*");
        //then
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.name"), Matchers.is(fileName)))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.type"), Matchers.is(contentType)))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data"), Matchers.is(new String(data, StandardCharsets.UTF_8))));

    }

    @Transactional
    @Test
    void Given_AllFilesInFolderSelected_When_StringArrayOfSelectedFileIds_Then_deleteAllSelectedIdsAndReturnEmpty() throws Exception {
        //given
        var user = User.builder()
                .email("test@test")
                .password("password123")
                .name("test")
                .authorities(Arrays.asList("ROLE_USER"))
                .build();
        userRepository.save(user);

        final MockMultipartFile multipartFile = new MockMultipartFile("test.txt", "this is test".getBytes());
        var folder = new Folder("tester", user, new ArrayList<>());

        folderRepository.save(folder);

        var token = jwtUtility.generateToken(user.getEmail(), user.getAuthorities());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        var file1 = fileService.uploadFile(folder.getId(), multipartFile, token);
        var file2 = fileService.uploadFile(folder.getId(), multipartFile, token);
        var file3 = fileService.uploadFile(folder.getId(), multipartFile, token);


        var findCreatedFolder = folderRepository.findById(folder.getId()).orElseThrow();
        folderRepository.save(findCreatedFolder);

        List<String> listOfFileIds = List.of(
               String.valueOf(file1.getId()),
                String.valueOf(file2.getId()),
                String.valueOf(file3.getId())
                );

        var json = mapper.writeValueAsString(listOfFileIds);
        //when
        var request = MockMvcRequestBuilders
                .delete("/file/delete/many/{folderId}", String.valueOf(folder.getId()))
                .headers(headers)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.message"), Matchers.is("Files successfully deleted")))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data"), Matchers.empty()));

    }
}
