package com.example.rewrite.service.ordercart;

import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Orders;

import java.util.List;

public interface OrderCartService {

    List<OrderCart> getOrderCartItems(Orders order);
}
