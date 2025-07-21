package com.example.rewrite.repository.review;

import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByUserUid(Long uid);

    // 로그인한 유저(uid)가 해당 상품(prodId)에 대해 리뷰를 썼는지 확인
    boolean existsByUserUidAndProductProdId(Long uid, Long prodId);

    @Modifying
    @Transactional
    // Review 엔티티 내 Product 참조 필드 이름이 'product'이고, Product ID 필드가 'prodId'라고 가정
    @Query("DELETE FROM Review r WHERE r.product.prodId = :productId")
    void deleteByProductProdId(@Param("productId") Long productId);

    void deleteByProduct(Product product);

    void deleteByUserUid(Long uid);
}
