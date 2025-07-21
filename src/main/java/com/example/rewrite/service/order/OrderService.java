package com.example.rewrite.service.order;

import com.example.rewrite.command.OrderSummaryDto;
import com.example.rewrite.entity.Cart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    void saveOrder(Orders order, List<Cart> checkedCarts);//order에 저장
    List<Product> findOrderDetail(Long oid);
    Orders findByOrderId(Long oid);//주문 상세
    List<Product> getOrderAll(Long uid);
    List<Product> getOrder4(Long uid);//내 주문내역 아이템 전체 조회
    void updateOrderWithPaymentInfo(String orderId, String paymentKey, int amount, String paymentMethod, String paymentStatus, String approvedAt);
    //order에 저장

    List<OrderSummaryDto> getOrderSummaries(Long uid);//주문 내역
}

