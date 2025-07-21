package com.example.rewrite.controller; // 패키지 경로 확인!

import com.example.rewrite.command.ProductDTO;
import com.example.rewrite.service.prod.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {


    @Autowired
    private ProdService prodService;

    @GetMapping("/")
    public String home(Model model) {
        // 판매 완료되지 않은 상품 목록 가져오기
        List<ProductDTO> products = prodService.getActiveProducts("latest");

        // 메인 페이지에 표시할 상품 수 제한 (예: 8개)
        int limit = Math.min(products.size(), 8);
        List<ProductDTO> displayProducts = products.subList(0, limit);

        // 모델에 상품 목록 추가
        model.addAttribute("products", displayProducts);
        return "main";
    }


    @GetMapping("/home")
    public String home2() {
        return "home";
    }
}