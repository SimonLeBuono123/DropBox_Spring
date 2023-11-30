package com.example.dropboxSpring;

import com.example.dropboxSpring.dtos.MessageDto;
import com.example.dropboxSpring.models.Folder;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.FolderRepository;
import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.security.JwtUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


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
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FolderRepository folderRepository;

    @BeforeEach
    void beforeEach(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "folder_files","folder", "user", "files" );
    }
    @Test
    void uploadNewFileSuccess() throws Exception {
        //given
        var user = User.builder()
                .email("test@test")
                .password("password123")
                .name("test")
                .authorities(Arrays.asList("ROLE_USER"))
                .build();

        var folder = Folder.builder()
                .name("tests")
                .user(user)
                .build();
        userRepository.save(user);
        folderRepository.save(folder);

        final MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "this is a test".getBytes());

        String folderId = String.valueOf(folder.getId());
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
                .andExpect(MockMvcResultMatchers.jsonPath(("$.name"), Matchers.is("test.txt")))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.type"), Matchers.is("text/plain")))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data"), Matchers.is("this is a test")));


    }
}
