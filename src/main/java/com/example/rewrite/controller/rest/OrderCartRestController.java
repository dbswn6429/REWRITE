package com.example.rewrite.controller.rest;

import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.service.ordercart.OrderCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderCartRestController {

    @Autowired
    private OrderCartService orderCartService;

    // 특정 주문에 포함된 상품을 조회하는 API
    @GetMapping("/api/orders/{orderId}/items")
    public List<OrderCart> getOrderCartItems(@PathVariable Long orderId) {
        Orders order = new Orders();
        order.setOrderId(orderId); // 주문 ID로 Orders 객체 조회 (여기서는 단순화)
        return orderCartService.getOrderCartItems(order);
    }


}
