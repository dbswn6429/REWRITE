package com.example.rewrite.command.prod;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompleteDto {
    private String paymentKey;
    private String orderId;
    private int amount;
    private String paymentMethod;
    private String paymentStatus;
    private String approvedAt;
}
