package com.example.dropboxSpring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class FolderDoesNotContainFileException extends Exception{
    public FolderDoesNotContainFileException(String message) {
        super(message);
    }
}
