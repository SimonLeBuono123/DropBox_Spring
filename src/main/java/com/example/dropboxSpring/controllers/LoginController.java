package com.example.dropboxSpring.controllers;

import com.example.dropboxSpring.dtos.LoginDto;
import com.example.dropboxSpring.services.LoginService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginDto loginDto
            ){
        return ResponseEntity.ok(loginService.login(loginDto));
    }


    @GetMapping("/adminTest")
    public ResponseEntity<String> testIfAdmin(){
        return ResponseEntity.ok("Welcome to the admin lounge!");
    }
}
