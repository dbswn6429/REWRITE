package com.example.rewrite.service.wishlist;

import com.example.rewrite.entity.Wishlist;

import java.util.List;

public interface WishlistService {

    /* 특정 사용자의 모든 찜 목록을 조회하는 메소드*/
    List<Wishlist> getWishlistByUserId(Long uid);

    /* 찜하기/취소 토글 기능을 제공하는 메소드 */
    boolean toggleWishlist(Long uid, Long prodId);

    /* 특정 사용자가 특정 상품을 찜했는지 확인하는 메소드*/
    boolean isWishlisted(Long uid, Long prodId);

    /* 특정 상품의 찜 개수를 조회하는 메소드 */
    int getWishlistCount(Long prodId);

}
