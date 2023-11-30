package com.example.dropboxSpring.dtos;

/*
Primary reason of this get the value from data to use it when I'm testing
and comparing results
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDto {
    private UUID id;
    private String name;
    private String type;
    private String data;
}
