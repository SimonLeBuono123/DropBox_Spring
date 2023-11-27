package com.example.dropboxSpring.exceptions;

import com.example.dropboxSpring.dtos.MessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<MessageDto> handleMaxSizeException(MaxUploadSizeExceededException e){
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDto("File is too large!"));
    }
}
