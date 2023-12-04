package com.example.dropboxSpring.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Dto for when logging-in on a user
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDto {
     String email;
     String password;
}
