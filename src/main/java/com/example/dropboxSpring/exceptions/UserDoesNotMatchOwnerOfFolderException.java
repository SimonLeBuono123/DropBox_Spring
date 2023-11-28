package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDoesNotMatchOwnerOfFolderException extends RuntimeException{
    public UserDoesNotMatchOwnerOfFolderException(String message) {
        super(message);
    }
}
