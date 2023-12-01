package com.example.dropboxSpring.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MessageDto {
    private String message;
    private Object data;

    public MessageDto(String message) {
        this.message = message;
    }
}
