package com.example.rewrite.service.review;

import com.example.rewrite.command.ReviewDTO;
import com.example.rewrite.entity.Review;
import com.example.rewrite.repository.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<ReviewDTO> getReviewsByUid(Long uid) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Review> reviews = reviewRepository.findByUserUid(uid);
        return reviews.stream()
                .map(r -> ReviewDTO.builder()
                        .id(r.getReviewId() != null ? r.getReviewId() : null)
                        .title(r.getProduct().getTitle())
                        .writer(r.getUser().getNickname()) // 또는 getUsername()
                        .createdDate(r.getRegDate() != null ? r.getRegDate().format(formatter) : "")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO getReviewById(Integer reviewId) {
        // 1. 리뷰 엔티티를 PK로 조회
        Review review = reviewRepository.findById(reviewId).orElse(null);

        // 2. 리뷰가 없으면 null 반환
        if (review == null) return null;

        // 3. 엔티티 → DTO 변환
        return ReviewDTO.builder()
                .id(review.getReviewId() != null ? review.getReviewId() : null) // Integer → Long
                .title(review.getProduct().getTitle()) // 상품명
                .content(review.getContent())          // 내용
                .createdDate(
                        review.getRegDate() != null
                                ? review.getRegDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                : ""
                ) // 작성일 (yyyy-MM-dd)
                .writer(review.getUser().getNickname()) // 작성자 (필요에 따라 수정)
                .build();
    }




}
