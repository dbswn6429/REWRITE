package com.example.rewrite.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_uid", nullable = false)
    private Users buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id",nullable = false)
    private Address address;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;
    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;
    @Column(name = "postcode", length = 10)
    private String postcode;
    @Column(name = "addr", length = 255)
    private String addr;
    @Column(name = "detail_addr",  length = 255)
    private String detailAddr;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;
    @Column(name = "shipping_fee", nullable = false)
    private int shippingFee;
    @Column(name = "final_price", nullable = false)
    private int finalPrice;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;  // 주문 시각

    @Column(name = "delivery_request")
    private String deliveryRequest;

    @Column(name = "order_status", length = 20)
    private String orderStatus;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus; // READY, PAID, CANCELED 등

    @Column(name = "payment_key", length = 100)
    private String paymentKey;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "toss_order_id", length = 100)
    private String tossOrderId;
}
