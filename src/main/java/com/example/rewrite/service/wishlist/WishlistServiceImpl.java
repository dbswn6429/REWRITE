package com.example.rewrite.service.wishlist;


import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import com.example.rewrite.entity.Wishlist;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.users.UsersRepository;
import com.example.rewrite.repository.wishlist.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Wishlist> getWishlistByUserId(Long uid) {
        // 사용자의 찜 목록 조회
        return wishlistRepository.findByUserUid(uid);
    }

    @Override
    @Transactional
    public boolean toggleWishlist(Long uid, Long prodId) {
        // 이미 찜했는지 확인
        boolean exists = wishlistRepository.existsByUserUidAndProductProdId(uid, prodId);

        if (exists) {
            // 이미 찜한 경우: 찜 취소
            wishlistRepository.deleteByUserUidAndProductProdId(uid, prodId);
            return false;  // 찜 취소됨
        } else {
            // 찜하지 않은 경우: 찜 추가

            // 사용자 조회 - uid 필드로 조회
            Users user = usersRepository.findById(uid)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 상품 조회 - prod_id 필드로 조회
            Product product = productRepository.findById(prodId)
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            // 새 찜 객체 생성 및 저장
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setProduct(product);

            wishlistRepository.save(wishlist);
            return true;  // 찜 추가됨
        }
    }

    @Override
    public boolean isWishlisted(Long uid, Long prodId) {
        // 사용자가 해당 상품을 찜했는지 확인
        return wishlistRepository.existsByUserUidAndProductProdId(uid, prodId);
    }

    @Override
    public int getWishlistCount(Long prodId) {
        // 상품의 찜 개수 조회
        return wishlistRepository.countByProductProdId(prodId);
    }
}
