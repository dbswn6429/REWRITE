package com.example.rewrite.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PRODUCT")
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PROD_ID",nullable = false ,length = 255)
    private Long prodId;

    @Column(name = "PRICE")
    private String price;

    @Column(name="TITLE", length = 255)
    private String title;

    @Column(name="DESCRIPTION" , length = 1000)
    private String description;

    @Column(name="PROD_STATUS",length = 50)
    private String prodStatus;

    @Column(name = "VIEWCOUNT")
    private Integer viewCount;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "PICKUP_ADDRESS", length = 100)
    private String pickupAddress;

    @Column(name = "PICKUP_DATE")
    private LocalDateTime pickupDate;

    @Column(name = "PICKUP_STATUS", length = 50)
    private String pickupStatus;

    @Column(name = "VIDEO_URL", length = 200)
    private String videoUrl;

    @Column(name = "CATEGORY_MAX", length = 50)
    private String categoryMax;

    @Column(name = "CATEGORY_MIN", length = 50)
    private String categoryMin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UID", referencedColumnName = "uid", nullable = false)
    private Users user;

    @Column(name = "IMG_1", length = 200)
    private String img1;

    @Column(name = "IMG_2", length = 200)
    private String img2;

    @Column(name = "IMG_3", length = 200)
    private String img3;

    @Column(name = "IMG_4", length = 200)
    private String img4;

//    @OneToMany
//    private List<OrderCart> orderCarts;
}
