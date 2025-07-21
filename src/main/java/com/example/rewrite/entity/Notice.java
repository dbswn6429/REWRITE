package com.example.rewrite.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"content"})
@NoArgsConstructor
@Entity
@Table(name = "notice")
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", nullable = false, updatable = false)
    private Long noticeId;

    @Column(name = "title", length = 200, nullable = true)
    private String title;

    @Lob
    @Column(name = "content", nullable = true)
    private String content;

    @Column(name = "img", length = 255, nullable = true)
    private String img;

    @CreationTimestamp // 엔티티 생성 시 현재 시간 자동 저장
    @Column(name = "reg_date", nullable = true, updatable = false)
    private LocalDateTime regDate; //

    // 생성자 (Lombok @NoArgsConstructor가 기본 생성자를 만듦)
    // 필요하다면 필드를 받는 생성자를 둘 수 있습니다.
    // Qna의 생성자와 형식을 맞추기 위해 기존 생성자를 유지합니다.
    public Notice(String title, String content, String img) {
        this.title = title;
        this.content = content;
        this.img = img;
    }
}