package com.example.dropboxSpring.exceptions;

/**
 * Exception class for handling files that does
 * not exist.
 */
public class FileDoesNotExistException extends RuntimeException{
    public FileDoesNotExistException(String message) {
        super(message);
    }
}
