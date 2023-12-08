package com.example.dropboxSpring;

import com.example.dropboxSpring.dtos.RegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application-test.properties")
public class UserIntegrationTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void beforeEach(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "folder_files","folder", "user", "files" );

    }

    /**
     * This is a method for testing when registering a new user.
     * Here it tests when I add all the credentials(name, email, password) and expects an
     * Ok response with all given credentials matching with expected result and also a success message.
     * @throws Exception
     */
    @Test
    void given_iHaveSelectedAllMyCredentials_when_registerWithSelectedCredentials_then_iGetAnResponseStatusOkAndExpectAnMessageDtoObjectWithAllItsValuesMatching() throws Exception {
        //given
        var email = "test@test.com";
        var name = "test";
        var password = "password123";
        var role = "ROLE_USER";

        var registerDto = new RegisterDto();

        registerDto.setEmail(email);
        registerDto.setName(name);
        registerDto.setPassword(password);

        var json = mapper.writeValueAsString(registerDto);
        //when

        var request = MockMvcRequestBuilders
                .post("/user/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //then

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.message"), Matchers.is("User created successfully!")))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data.name"), Matchers.is(name)))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data.email"), Matchers.is(email)))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data.roles"), Matchers.contains(role)));

    }
}
