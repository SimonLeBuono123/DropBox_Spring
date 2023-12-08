package com.example.dropboxSpring.services;

import com.example.dropboxSpring.dtos.LoginDto;
import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Class for providing business functionality when logging in to
 * user.
 */

@Service
@RequiredArgsConstructor
public class LoginService {


    private final UserRepository userRepo;
    private final AuthenticationManager manager;
    private final JwtUtility jwtUtility;

    /**
     * Method for logging-in on an already created user and generating a token
     * to user as authorization when creating folders and files.
     *
     * @param loginDto
     * @return
     */
    public String login(LoginDto loginDto) {

        manager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword())
        );

        var user = userRepo.findByEmail(loginDto.getEmail()).orElseThrow();

        String token = jwtUtility.generateToken(user.getUsername(), user.getAuthorities());
        return token;
    }

}
