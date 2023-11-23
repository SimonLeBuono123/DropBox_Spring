package com.example.dropboxSpring.dtos;


import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class dtoFile {
    private String name;
    private String url;
    private String type;
    private long size;
}
