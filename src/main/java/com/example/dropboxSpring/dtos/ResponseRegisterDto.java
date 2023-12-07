package com.example.dropboxSpring.dtos;

import lombok.Data;

import java.util.List;

@Data

public class ResponseRegisterDto {

    private String name;
    private String email;
    private List<String> roles;

}
