package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for handling when the given user
 * does not match with the owner of a folder.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDoesNotMatchOwnerOfFolderException extends RuntimeException{
    public UserDoesNotMatchOwnerOfFolderException(String message) {
        super(message);
    }
}
