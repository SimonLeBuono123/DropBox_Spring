package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for handling when a file is not
 * part of a given folder.
 */

@ResponseStatus(HttpStatus.FORBIDDEN)
public class FolderDoesNotContainFileException extends Exception{
    public FolderDoesNotContainFileException(String message) {
        super(message);
    }
}
