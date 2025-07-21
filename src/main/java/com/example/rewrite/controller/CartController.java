package com.example.rewrite.controller;

import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long prodId, HttpSession session, RedirectAttributes redirectAttributes) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        try {
            cartService.addCart(user.getUid(), prodId);
            redirectAttributes.addFlashAttribute("message", "상품이 장바구니에 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 추가에 실패했습니다: " + e.getMessage());
        }

        return "redirect:/prod/prodDetail?prodId=" + prodId;
    }
}