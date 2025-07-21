package com.example.rewrite.command; // 실제 프로젝트 패키지 경로로 수정하세요.

import lombok.*;

import java.time.LocalDateTime; // java.sql.Timestamp 대신 권장되는 타입

@Data
@NoArgsConstructor // MyBatis 등 프레임워크에서 객체 생성 시 필요
@AllArgsConstructor // 모든 필드를 받는 생성자 (선택 사항)
public class NoticeVO {

    private Long noticeId;
    private String title;
    private String content;
    private String img;
    private LocalDateTime regDate; // MySQL TIMESTAMP와 매핑 (MyBatis 최신 버전에서 잘 지원)

}