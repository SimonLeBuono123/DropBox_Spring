package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.RegisterDto;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param registerDto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestBody RegisterDto registerDto
            ){
        return ResponseEntity.ok(userService.register(registerDto));
    }
}
