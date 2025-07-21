package com.example.rewrite.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
public class OrderSummaryDto {

    private Long orderId;
    private String orderStatus;
    private LocalDateTime orderedAt;
    private String receiverPhone;
    private String receiverName;
    private int finalPrice;

    private Long prodId;
    private String price;
    private String img1;
    private String description;
    private String title;

    private String sellerNickname;
}
