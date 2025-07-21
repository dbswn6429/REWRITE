package com.example.rewrite.controller;


import com.example.rewrite.command.ReviewDTO;
import com.example.rewrite.command.UserVO;
import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Review;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.review.ReviewRepository;
import com.example.rewrite.repository.users.UsersRepository;
import com.example.rewrite.service.review.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService, UsersRepository usersRepository, ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/reviewDetail")
    public String reviewDetail(@RequestParam("reviewId") Integer reviewId, Model model) {
        ReviewDTO reviewDetailDto = reviewService.getReviewById(reviewId); // reviewId로 DTO 조회
        model.addAttribute("review", reviewDetailDto); // 모델에 담기
        return "review/reviewDetail";
    }

    @GetMapping("/reviewList")
    public String reviewList(@RequestParam(value = "uid", required = false) Long uid,
                             HttpSession session, Model model) {
        // uid 파라미터가 있으면 해당 판매자 리뷰, 없으면 로그인 유저 리뷰
        if (uid == null) {
            UserSessionDto user = (UserSessionDto) session.getAttribute("user");
            if (user == null) {
                return "redirect:/user/login";
            }
            uid = user.getUid();
        }

        // 리뷰 리스트 조회
        List<ReviewDTO> reviews = reviewService.getReviewsByUid(uid);
        model.addAttribute("reviews", reviews);

        // 판매자 정보도 필요하면 모델에 추가
        Users seller = usersRepository.findById(uid).orElse(null);
        model.addAttribute("seller", seller);

        return "review/reviewList";
    }

    @GetMapping("/review")
    public String writeReview(@RequestParam("prodId") Long prodId,
                              @RequestParam("title") String title,
                              Model model) {
        model.addAttribute("prodId", prodId);
        model.addAttribute("title", title);
        return "review/review";
    }

    @PostMapping("/save")
    public String saveReview(@RequestParam("prodId") Long prodId,
                             @RequestParam("content") String content,
                             HttpSession session) {
        // 1. 현재 로그인 유저 정보 가져오기
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/user/login";
        }
        Long uid = userSession.getUid();

        // 2. 엔티티 조회
        Users user = usersRepository.findById(uid).orElseThrow();
        Product product = productRepository.findById(prodId).orElseThrow();

        // 3. 리뷰 엔티티 생성
        Review review = new Review();
        review.setContent(content);
        review.setRegDate(LocalDateTime.now());
        review.setUser(user);
        review.setProduct(product);

        // 4. 저장
        reviewRepository.save(review);

        return "redirect:/review/reviewList";
    }
}