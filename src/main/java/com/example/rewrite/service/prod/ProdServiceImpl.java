package com.example.rewrite.service.prod;

import com.example.rewrite.command.ProductDTO;
import com.example.rewrite.entity.Address;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.cart.CartRepository;
import com.example.rewrite.repository.ordercart.OrderCartRepository;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.review.ReviewRepository;
import com.example.rewrite.repository.users.UsersRepository;
import com.example.rewrite.repository.wishlist.WishlistRepository;
import com.example.rewrite.service.address.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public  class ProdServiceImpl implements ProdService {

    private final ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private OrderCartRepository orderCartRepository;
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private AddressService addressService;
    @Autowired
    private ReviewRepository reviewRepository;



    @Autowired
    public ProdServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDTO> getAllProducts(String sortBy) {
        Sort sort;

        // 정렬 기준에 따라 Sort 객체 생성
        switch(sortBy) {
            case "latest":
                sort = Sort.by(Sort.Direction.DESC, "regDate"); // 최신순
                break;
            case "views":
                sort = Sort.by(Sort.Direction.DESC, "viewCount"); // 조회수순
                break;
            case "priceAsc":
                sort = Sort.by(Sort.Direction.ASC, "price"); // 가격 낮은순
                break;
            case "priceDesc":
                sort = Sort.by(Sort.Direction.DESC, "price"); // 가격 높은순
                break;
            default:
                sort = Sort.by(Sort.Direction.DESC, "regDate"); // 기본값은 최신순
        }

        return productRepository.findAll(sort).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getActiveProducts(String sortBy) {
        Sort sort;
        if ("latest".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "regDate"); // 최신순
        } else {
            sort = Sort.by(Sort.Direction.ASC, "prodPrice"); // 가격순
        }

        // prodStatus가 "판매완료"가 아닌 상품만 조회
        List<Product> productList = productRepository.findByProdStatusNot("판매완료", sort);

        return productList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    // 내 상품 목록 조회
    @Override
    public List<ProductDTO> getMyProducts(Long uid) {
        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        List<Product> myProducts = productRepository.findProductsByUserUid(uid, sort);

        return myProducts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 상품 상세 조회
    @Override
    @Transactional
    public ProductDTO getProductById(Long id) {
        // 조회수 증가
        productRepository.incrementViewCount(id);

        // 사용자 정보와 함께 상품 조회
        Product product = productRepository.findProductWithUserById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        ProductDTO productDTO = ProductDTO.fromEntity(product);
        return productDTO;
    }

    // 상품 등록
    @Override
    @Transactional
    public ProductDTO registerProduct(ProductDTO productDTO) {
        // 사용자 엔티티 조회
        Users user = userRepository.findById(productDTO.getUid())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 1. 기본 주소 조회
        String pickupAddress = productDTO.getPickupAddress();
        if (pickupAddress == null || pickupAddress.trim().isEmpty()) {
            Address defaultAddress = addressService.getDefaultAddress(user.getUid());
            pickupAddress = (defaultAddress != null) ? defaultAddress.getAddress() : null;
        }

        // 2. 상품 엔티티 생성
        Product product = Product.builder()
                .title(productDTO.getTitle())
                .categoryMax(productDTO.getCategoryMax())
                .categoryMin(productDTO.getCategoryMin())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .img1(productDTO.getImg1())
                .img2(productDTO.getImg2())
                .img3(productDTO.getImg3())
                .img4(productDTO.getImg4())
                .videoUrl(productDTO.getVideoUrl())
                .regDate(LocalDateTime.now())
                .pickupAddress(pickupAddress) // 기본 주소 저장
                .pickupStatus("픽업대기중")      // 추가: 픽업상태 기본값
                .prodStatus("판매중")            // 추가: 상품상태 기본값
                .user(user)
                .build();

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    // 엔티티를 DTO로 변환
    private ProductDTO convertToDto(Product product) {
        ProductDTO.ProductDTOBuilder build = ProductDTO.builder()
                .prodId(Long.valueOf(product.getProdId()))
                .title(product.getTitle())
                .categoryMax(product.getCategoryMax())
                .categoryMin(product.getCategoryMin())
                .price(product.getPrice())
                .description(product.getDescription())
                .img1(product.getImg1())
                .img2(product.getImg2())
                .img3(product.getImg3())
                .img4(product.getImg4())
                .videoUrl(product.getVideoUrl())
                .regDate(product.getRegDate())
                .pickupStatus(product.getPickupStatus())
                .prodStatus(product.getProdStatus())
                .viewCount(product.getViewCount() != null ? product.getViewCount() : 0L);

        if (product.getUser() != null) {
            build.uid(product.getUser().getUid())
                    .userNickname(product.getUser().getNickname())
                    .userImgUrl(product.getUser().getImgUrl());
        }

        return build.build();
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product product = productRepository.findById(Long.valueOf(productDTO.getProdId()))
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 변경 가능한 필드 업데이트
        product.setTitle(productDTO.getTitle());
        product.setCategoryMax(productDTO.getCategoryMax());
        product.setCategoryMin(productDTO.getCategoryMin());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setImg1(productDTO.getImg1());
        product.setImg2(productDTO.getImg2());
        product.setImg3(productDTO.getImg3());
        product.setImg4(productDTO.getImg4());
        product.setVideoUrl(productDTO.getVideoUrl());
        product.setPickupAddress(productDTO.getPickupAddress());

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    @Override
    public List<ProductDTO> searchProductByUid(Long uid) {
        
        //uid 없는경우에는 전체 상품 땡겨옴
        if (uid == null) {
            List<Product> products = productRepository.findAll();
            List<ProductDTO> list = new ArrayList<>();
            for (Product product : products) {
                ProductDTO dto = ProductDTO.fromEntity(product);
                list.add(dto);
            }
            return list;
        } else {
            List<Product> products = productRepository.findProductsByUserUid(uid);
            List<ProductDTO> list = new ArrayList<>();
            for (Product product : products) {
                ProductDTO dto = ProductDTO.fromEntity(product);
                list.add(dto);
            }
            return list;
        }
    }

    @Override
    public List<ProductDTO> getSellerProducts(Long uid, String sortBy) {
        Sort sort;
        switch (sortBy) {


            case "views":
                sort = Sort.by(Sort.Direction.DESC, "viewCount");
                break;
            case "priceAsc":
                sort = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "priceDesc":
                sort = Sort.by(Sort.Direction.DESC, "price");
                break;
            case "latest":
            default:
                sort = Sort.by(Sort.Direction.DESC, "regDate");
        }
        List<Product> products = productRepository.findAllByUser_Uid(uid, sort);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        //delete할때 service단에서 포린키 제약 걸린애들 먼저 날림
        reviewRepository.deleteByProductProdId(id);
        orderCartRepository.deleteByProductProdId(id);
        wishlistRepository.deleteByProductProdId(id);
        cartRepository.deleteByProductProdId(id);
        productRepository.deleteById(id);
    }

    @Transactional
    public void bumpProduct(Long prodId) {
        Product product = productRepository.findById(prodId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        product.setRegDate(LocalDateTime.now()); // 작성일자를 현재로 변경
        productRepository.save(product);
    }

    @Override
    public void updateProductStatus(Long prodId, String ProdStatus) {
        Product product = productRepository.findById(prodId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setProdStatus(ProdStatus);

        productRepository.save(product);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category, String sortBy) {
        Sort sort;

        // 정렬 기준에 따라 Sort 객체 생성 (기존 코드와 동일한 로직)
        switch(sortBy) {
            case "latest":
                sort = Sort.by(Sort.Direction.DESC, "regDate"); // 최신순
                break;
            case "views":
                sort = Sort.by(Sort.Direction.DESC, "viewCount"); // 조회수순
                break;
            case "priceAsc":
                sort = Sort.by(Sort.Direction.ASC, "price"); // 가격 낮은순
                break;
            case "priceDesc":
                sort = Sort.by(Sort.Direction.DESC, "price"); // 가격 높은순
                break;
            default:
                sort = Sort.by(Sort.Direction.DESC, "regDate"); // 기본값은 최신순
        }

        // 카테고리와 정렬 조건으로 상품 조회
        return productRepository.findByCategoryMax(category, sort).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }




    @Override
    public List<Product> searchProductsByTitle(String keyword) {
        return productRepository.findByTitleContaining(keyword);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
