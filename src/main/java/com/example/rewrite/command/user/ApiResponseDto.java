package com.example.rewrite.command.user;

import lombok.Getter;

@Getter
public class ApiResponseDto {
    private final boolean success;
    private final String message;

    // 생성자
    public ApiResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 정적 팩토리 메서드 (편의용)
    public static ApiResponseDto success(String message) {
        return new ApiResponseDto(true, message);
    }

    public static ApiResponseDto fail(String message) {
        return new ApiResponseDto(false, message);
    }
}