package com.example.rewrite.service.ordercart;

import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.repository.ordercart.OrderCartRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderCartServiceImpl implements OrderCartService {

    @Autowired
    private OrderCartRepository orderCartRepository;

    @Override
    public List<OrderCart> getOrderCartItems(Orders orders) {
        return orderCartRepository.findByOrders(orders);
    }
}
