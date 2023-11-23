package com.example.dropboxSpring.services;

import com.example.dropboxSpring.dtos.RegisterDto;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public User register(RegisterDto registerDto){
        var user = User.builder()
                .name(registerDto.getName())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .authorities(Arrays.asList("ROLE_ADMIN"))
                .build();

        return userRepository.save(user);
    }
}
