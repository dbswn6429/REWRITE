package com.example.rewrite.service.review;

import com.example.rewrite.command.ReviewDTO;
import com.example.rewrite.entity.Review;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getReviewsByUid(Long uid);

    ReviewDTO getReviewById(Integer reviewId);

}
