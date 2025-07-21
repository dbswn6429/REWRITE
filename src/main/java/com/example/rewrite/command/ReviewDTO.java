package com.example.rewrite.command;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReviewDTO {
    private Integer id; //번호
    private String title; //상품명
    private String content; //내용
    private Double rating; //별점 - 안씀
    private String createdDate; //작성일
    private String writer;

}
