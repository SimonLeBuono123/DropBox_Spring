package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for when a folder does not exist.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FolderDoesNotExistException extends RuntimeException{
    public FolderDoesNotExistException(String message) {
        super(message);
    }
}
