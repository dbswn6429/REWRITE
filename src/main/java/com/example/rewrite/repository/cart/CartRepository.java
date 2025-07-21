package com.example.rewrite.repository.cart;

import com.example.rewrite.command.ProductDTO;
import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Cart;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_Uid(Long uid);

    void deleteByCartIdAndUser_Uid(Long cartId, Long uid);
    void deleteByUserUid(Long uid);
    List<Cart> findCartsByProduct(Product product);

    // 추가할 메서드들
    // 1. 선택된 장바구니 아이템만 조회 (체크된 상품)
    List<Cart> findByUser_UidAndIsCheckedTrue(Long uid);

    void deleteByProduct(Product product);
    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.product.prodId = :prodId")
    void deleteByProductProdId(@Param("prodId") Long prodId);
}
