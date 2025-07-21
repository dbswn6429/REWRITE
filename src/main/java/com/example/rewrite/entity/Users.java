package com.example.rewrite.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp; // Hibernate 사용 시


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    private Long uid;

    @Column(name = "id", nullable = false, unique = true, length = 50)
    private String id;

    @Column(name = "pw", nullable = false, length = 255)
    private String pw;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "nickname", length = 30)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "birth") // DATE 타입은 LocalDate와 매핑
    private LocalDate birth;


    @Column(name = "img_url", length = 512)
    @Builder.Default
    private String imgUrl = "https://storage.googleapis.com/rewrite_project/profile_img/user_default.png";

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    @Column(name = "phone", length = 25)
    private String phone;

    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default // **** 이 어노테이션 추가 ****
    private String role = "user"; // 자바 객체 생성 시 기본값
}