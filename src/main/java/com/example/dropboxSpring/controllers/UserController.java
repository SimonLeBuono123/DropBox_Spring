package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.MessageDto;
import com.example.dropboxSpring.dtos.RegisterDto;
import com.example.dropboxSpring.dtos.ResponseRegisterDto;
import com.example.dropboxSpring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class for handling different http methods and routes
 * for the model entity user
 */
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Http post method for creating a new user
     *
     * @param registerDto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<MessageDto> registerUser(
            @RequestBody RegisterDto registerDto
    ) {
        String message = "";
        try {

            ResponseRegisterDto responseDto = userService.register(registerDto);
            message = "User created successfully!";

            return ResponseEntity.ok(new MessageDto(message, responseDto));
        } catch (Exception e) {

            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(message));
        }

    }
}
