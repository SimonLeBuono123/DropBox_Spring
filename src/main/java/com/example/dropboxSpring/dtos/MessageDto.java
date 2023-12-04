package com.example.dropboxSpring.dtos;

import lombok.*;

/**
 * Dto response messages and also for data if needed
 * Mostly for when you need to return either a successful message or
 * an error exception message
 */
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
