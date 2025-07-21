package com.example.rewrite.controller;

import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Product;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.service.prod.ProdService;
import com.example.rewrite.service.wishlist.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private ProdService prodService;

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String keyword, Model model, HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");

        List<Product> products;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 서비스 계층을 통해 상품 검색
            products = prodService.searchProductsByTitle(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            // 검색어가 없으면 전체 상품 표시
            products = prodService.getAllProducts();
        }

        model.addAttribute("products", products);

        // 찜 여부를 담을 wishMap 생성
        Map<Long, Boolean> wishMap = new HashMap<>();

        if (user != null) { // 로그인 되어있을 경우
            Long uid = user.getUid();
            for (Product product : products) {
                boolean isWished = wishlistService.isWishlisted(uid, product.getProdId());
                wishMap.put(product.getProdId(), isWished);
            }
        }

        model.addAttribute("wishMap", wishMap);

        return "prod/prodList"; // 기존 상품 목록 템플릿 사용
    }


}
