package com.example.rewrite.controller.rest;

import com.example.rewrite.command.ProductDTO;
import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Address;
import com.example.rewrite.service.address.AddressService;
import com.example.rewrite.service.prod.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProdService productService;
    private final AddressService addressService;

    @Autowired
    public ProductApiController(ProdService productService, AddressService addressService) {
        this.productService = productService;
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "latest") String sortBy) {
        List<ProductDTO> products = productService.getAllProducts(sortBy);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        System.out.println(product);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO, HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // uid 세팅
        productDTO.setUid(user.getUid());

        // ① 유저의 주소 조회 (예: 기본 주소)
        // 예시: AddressService가 있다면
        String pickupAddress = null;
        Address defaultAddress = addressService.getDefaultAddress(user.getUid());
        if (defaultAddress != null) {
            pickupAddress = defaultAddress.getAddress();
        }

        // 상품 등록
        ProductDTO createdProduct = productService.registerProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // 추가로 필요한 PUT, DELETE 메소드도 구현 필요
}
