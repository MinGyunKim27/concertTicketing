package org.example.concertTicketing.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<> (true, message, data, LocalDateTime.now().toString());
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now().toString());
    }
}
