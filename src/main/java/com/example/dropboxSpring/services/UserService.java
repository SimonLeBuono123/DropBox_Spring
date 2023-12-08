package com.example.dropboxSpring.services;

import com.example.dropboxSpring.dtos.RegisterDto;
import com.example.dropboxSpring.dtos.ResponseRegisterDto;
import com.example.dropboxSpring.models.User;
import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtility jwtUtility;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    /**
     * Method for creating a new user.
     *
     * @param registerDto
     * @return
     */
    public ResponseRegisterDto register(RegisterDto registerDto) {

        var user = User.builder()
                .name(registerDto.getName())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .authorities(Arrays.asList("ROLE_USER"))
                .build();

        ResponseRegisterDto responseDto = new ResponseRegisterDto();
        responseDto.setEmail(user.getEmail());
        responseDto.setName(user.getName());
        responseDto.setRoles(Arrays.asList("ROLE_USER"));

        userRepository.save(user);

        return responseDto;
    }

    /**
     * Method for getting the user by extracting the email
     * from a valid token
     *
     * @param token
     * @return
     */
    public User findUserByToken(String token) {
        // extract the users email from the token.
        String userEmail = jwtUtility.extractEmail(token);

        return userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Invalid email: " + userEmail));
    }
}
