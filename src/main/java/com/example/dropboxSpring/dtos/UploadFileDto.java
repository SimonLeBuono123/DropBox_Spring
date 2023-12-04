package com.example.dropboxSpring.dtos;

/*
Primary reason of this get the value from data to use it when I'm testing
and comparing results
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Dto for a response to when uploading a file and seeing the full File class but
 * instead of the getting the data with bytes you get instead the content of the file.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDto {
    private UUID id;
    private String name;
    private String type;
    private String data;
}
