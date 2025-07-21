package com.example.rewrite.service.prod;

import com.example.rewrite.command.ProductDTO;
import com.example.rewrite.entity.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ProdService {

    // 상품 목록 조회
    List<ProductDTO> getAllProducts(String sortBy);

    // 판매 완료되지 않은 상품만 조회
    List<ProductDTO> getActiveProducts(String sortBy);

    // 내 상품 목록 조회
    List<ProductDTO> getMyProducts(Long uid);

    // 상품 상세 조회
    ProductDTO getProductById(Long id);

    // 상품 등록
    ProductDTO registerProduct(ProductDTO productDTO);

    // 상품 수정
    ProductDTO updateProduct(ProductDTO productDTO);

    List<ProductDTO> searchProductByUid(Long uid);

    List<ProductDTO> getSellerProducts(Long uid, String sortBy);

    // 상품 삭제
    void deleteProduct(Long id);

    //끌어 올리기
    void bumpProduct(Long prodId);

    //결제되면 판매완료 처라
    void updateProductStatus(Long prodId, String prodStatus);

    List<ProductDTO> getProductsByCategory(String category, String sortBy);

    List<Product> searchProductsByTitle(String keyword);

    List<Product> getAllProducts();
}

