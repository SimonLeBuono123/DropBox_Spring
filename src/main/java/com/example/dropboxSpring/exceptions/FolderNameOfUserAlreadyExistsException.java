package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for when a folder of a user with given folder name already
 * exists
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FolderNameOfUserAlreadyExistsException extends Exception{
    public FolderNameOfUserAlreadyExistsException(String message) {
        super(message);
    }
}
