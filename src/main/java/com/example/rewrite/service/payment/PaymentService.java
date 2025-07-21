package com.example.rewrite.service.payment;

import com.example.rewrite.entity.Orders;
import com.example.rewrite.entity.Users;

import java.time.LocalDateTime;

public interface PaymentService {

    void recordPaymentHistory(Orders order, Users buyer, String paymentKey,
                              String status, String paymentMethod,
                              LocalDateTime paymentDateTime);


}
