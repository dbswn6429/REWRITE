package com.example.rewrite.service.payment;

import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.order.OrderRepository;
import com.example.rewrite.repository.ordercart.OrderCartRepository;
import com.example.rewrite.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderCartRepository orderCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void recordPaymentHistory(Orders order, Users buyer, String paymentKey, String status, String paymentMethod, LocalDateTime paymentDateTime) {

        order.setPaymentKey(paymentKey);
        order.setPaymentStatus(status);
        order.setPaymentMethod(paymentMethod);
        order.setPaidAt(paymentDateTime);

        orderRepository.save(order);

        // 결제한 물품들의 상태를 '판매됨'으로 변경
        List<OrderCart> orderCarts = orderCartRepository.findByOrders(order);

        for (OrderCart orderCart : orderCarts) {
            // 상품의 상태를 '판매됨'으로 변경
            orderCart.getProduct().setProdStatus("판매됨");
            // 상품을 저장하여 상태 변경을 반영
            productRepository.save(orderCart.getProduct());
        }
    }
}
