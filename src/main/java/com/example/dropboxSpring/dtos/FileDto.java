package com.example.dropboxSpring.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Dto for when getting all the files of a given folder
 */
@Data
@AllArgsConstructor
@Builder
public class FileDto {
    private String name;
    private String url;
    private String type;
    private long size;
}
