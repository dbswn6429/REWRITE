package com.example.rewrite.command;

import com.example.rewrite.entity.Product;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductDTO {

    private Long prodId;
    private String categoryMax;
    private String categoryMin;
    private String title;
    private String description;
    private String pickupAddress;
    private LocalDateTime pickupDate;
    private String pickupStatus;
    private String prodStatus;
    private LocalDateTime regDate;
    private String price;
    private String videoUrl;
    private Long viewCount;
    private Long uid;
    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String userNickname; // 사용자 닉네임
    private String userImgUrl;   // 사용자 프로필 이미지

    private String imgPath;
    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .prodId(product.getProdId())
                .categoryMax(product.getCategoryMax())
                .categoryMin(product.getCategoryMin())
                .title(product.getTitle())
                .description(product.getDescription())
                .pickupAddress(product.getPickupAddress())
                .pickupDate(product.getPickupDate())
                .pickupStatus(product.getPickupStatus())
                .prodStatus(product.getProdStatus())
                .regDate(product.getRegDate())
                .price(product.getPrice())
                .videoUrl(product.getVideoUrl())
                .viewCount(product.getViewCount() != null ? product.getViewCount().longValue() : null) // Integer -> Long 변환
                .uid(product.getUser() != null ? product.getUser().getUid() : null) // Users 엔티티에서 uid 가져오기
                .img1(product.getImg1())
                .img2(product.getImg2())
                .img3(product.getImg3())
                .img4(product.getImg4())
                .userNickname(product.getUser() != null ? product.getUser().getNickname() : null) // Users에서 닉네임 가져오기
                .userImgUrl(product.getUser() != null ? product.getUser().getImgUrl() : null)
                .build();
    }

}
