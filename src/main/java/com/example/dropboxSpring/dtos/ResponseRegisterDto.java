package com.example.dropboxSpring.dtos;

import lombok.Data;

import java.util.List;

/**
 * Dto class for when getting the response
 * of http method for registering user and some information
 * about the user.
 */
@Data
public class ResponseRegisterDto {

    private String name;
    private String email;
    private List<String> roles;

}
