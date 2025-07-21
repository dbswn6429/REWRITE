package com.example.rewrite.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserVO {

    private Long uid; // 고유 사용자 ID (자동 증가) - 기본형 int 대신 Integer 사용 (null 표현 가능)
    private String id; // 로그인 아이디
    private String pw; // 비밀번호
    private String nickname; // 사용자 닉네임
    private String name; // 사용자 이름
    private LocalDate birth; // 생년월일 (java.time.LocalDate 사용)
    private String imgUrl; // 프로필 이미지 URL
    private LocalDateTime createdAt; // 가입일시 (java.time.LocalDateTime 사용)
    private String phone; // 전화번호
    private String role; // 사용자 역할 (예: "USER", "ADMIN")
}
