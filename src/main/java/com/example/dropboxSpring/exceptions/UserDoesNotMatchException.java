package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDoesNotMatchException extends RuntimeException{
    public UserDoesNotMatchException(String message) {
        super(message);
    }
}
