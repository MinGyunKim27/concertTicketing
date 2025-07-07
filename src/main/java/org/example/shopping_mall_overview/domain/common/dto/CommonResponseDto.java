package org.example.shopping_mall_overview.domain.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseDto<T> {
    private Boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> CommonResponseDto<T> ok(String message, T data) {
        return CommonResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> CommonResponseDto<T> error(String message, T data) {
        return CommonResponseDto.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
