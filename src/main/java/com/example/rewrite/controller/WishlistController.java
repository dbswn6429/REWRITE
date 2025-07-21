package com.example.rewrite.controller;

import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Wishlist;
import com.example.rewrite.service.wishlist.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * 찜하기 기능의 웹 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    /**
     * 사용자의 찜목록 페이지를 보여주는 메소드
     */
    @GetMapping
    public String getWishlistPage(Model model, HttpSession session) {
        // 로그인 확인
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        // 사용자의 찜목록 조회
        List<Wishlist> wishlistItems = wishlistService.getWishlistByUserId(user.getUid());
        model.addAttribute("wishlistItems", wishlistItems);

        return "wishlist/list";
    }

    /**
     * 찜하기/취소 기능을 처리하는 AJAX 요청 핸들러
     */
    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleWishlist(@RequestParam Long prodId, HttpSession session) {
        try {
            // 로그 추가
            System.out.println("위시리스트 토글 요청 - 상품 ID: " + prodId);

            UserSessionDto user = (UserSessionDto) session.getAttribute("user");
            if (user == null) {
                System.out.println("사용자 세션 없음");
                return ResponseEntity.status(401).body("로그인이 필요합니다");
            }

            System.out.println("사용자 ID: " + user.getUid() + ", 상품 ID: " + prodId);

            // 서비스 메소드 호출
            boolean isWishlisted = wishlistService.toggleWishlist(user.getUid(), prodId);

            System.out.println("위시리스트 토글 결과: " + isWishlisted);

            Map<String, Object> response = new HashMap<>();
            response.put("isWishlisted", isWishlisted);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 오류 출력
            return ResponseEntity.status(500).body("오류: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<?> checkWishlist(@RequestParam Long prodId, HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        boolean isWishlisted = wishlistService.isWishlisted(user.getUid(), prodId);
        Map<String, Object> response = new HashMap<>();
        response.put("isWishlisted", isWishlisted);
        return ResponseEntity.ok(response);
    }
}
