package com.example.dropboxSpring.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Dto for when registering a new user
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto {
    String name;
    String email;
    String password;
}
