package com.example.dropboxSpring.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class dtoMessage {
    private String message;
}
