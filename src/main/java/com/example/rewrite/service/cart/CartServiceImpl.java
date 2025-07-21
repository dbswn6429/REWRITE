package com.example.rewrite.service.cart;

import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Cart;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.cart.CartRepository;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UsersRepository usersRepository;


    @Override
    public int calculateTotalPrice(List<Cart> cartList) {
        int totalPrice = 0;

        for (Cart cart : cartList) {
            int price = Integer.parseInt(cart.getProduct().getPrice());
            totalPrice += price;
        }
        return totalPrice;
    }

    @Override
    @Transactional
    public void addCart(Long uid, Long prodId) {
        // 사용자 조회
        Users user = usersRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 상품 조회
        Product product = productRepository.findById(prodId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // Cart 객체 생성
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        // addedAt은 @CreationTimestamp 어노테이션에 의해 자동 설정

        // Cart 저장
        cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCarts(Long uid) {
        return cartRepository.findByUser_Uid(uid);
    }

    @Override
    @Transactional
    public void deleteCart(Long cartId, Long uid) {
        cartRepository.deleteByCartIdAndUser_Uid(cartId, uid);
    }

    @Override
    public List<Cart> getCheckedCarts(Long uid) {
        return cartRepository.findByUser_UidAndIsCheckedTrue(uid);
    }

    @Override
    public void updateCheckedStatus(Long cartId, Boolean isChecked) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setIsChecked(isChecked);
        cartRepository.save(cart);
    }

    @Override
    public void clearUserCart(Long uid) {
        List<Cart> userCartItems = cartRepository.findByUser_Uid(uid);
        cartRepository.deleteAll(userCartItems);
    }
}

