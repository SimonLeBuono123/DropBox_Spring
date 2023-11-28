package com.example.dropboxSpring.exceptions;

public class FileDoesNotExistException extends RuntimeException{
    public FileDoesNotExistException(String message) {
        super(message);
    }
}
