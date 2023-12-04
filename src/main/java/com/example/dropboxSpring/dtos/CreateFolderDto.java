package com.example.dropboxSpring.dtos;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Dto class for when creating a new folder
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFolderDto {
    String name;

}
