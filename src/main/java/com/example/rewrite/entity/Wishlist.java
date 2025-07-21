package com.example.rewrite.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 찜하기 정보를 저장하는 엔티티 클래스
 * 사용자와 상품 간의 찜하기 관계를 표현
 */
@Entity
@Table(name = "wishlist")
@Getter @Setter
@NoArgsConstructor
@ToString
public class Wishlist {

    /**
     * 찜 항목의 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 찜을 등록한 사용자
     * users 테이블의 uid와 연결
     */
    @ManyToOne
    @JoinColumn(name = "uid")
    private Users user;

    /**
     * 찜한 상품
     * product 테이블의 prod_id와 연결
     */
    @ManyToOne
    @JoinColumn(name = "prod_id", referencedColumnName = "prod_id")
    private Product product;

    /**
     * 찜 등록 시간
     * 다른 테이블과 일관성을 위해 reg_date로 명명
     */
    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;
}
