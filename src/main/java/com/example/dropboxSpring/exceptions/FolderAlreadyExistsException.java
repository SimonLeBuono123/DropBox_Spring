package com.example.dropboxSpring.exceptions;

public class FolderAlreadyExistsException extends Exception{
    public FolderAlreadyExistsException(String message) {
        super(message);
    }
}
