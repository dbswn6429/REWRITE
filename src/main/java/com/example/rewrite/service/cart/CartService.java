package com.example.rewrite.service.cart;

import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Cart;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;

import java.util.List;

public interface CartService {
    int calculateTotalPrice(List<Cart> cartList);
    void addCart(Long uid, Long prodId);
    List<Cart> getCarts(Long uid);
    void deleteCart(Long cartId, Long uid);

    // 추가 메서드 - 주문/결제 페이지용
    // 체크된 장바구니 상품만 조회
    List<Cart> getCheckedCarts(Long uid);
    void updateCheckedStatus(Long cartId, Boolean isChecked);

    void clearUserCart(Long uid); //장바구니 비우기



}
