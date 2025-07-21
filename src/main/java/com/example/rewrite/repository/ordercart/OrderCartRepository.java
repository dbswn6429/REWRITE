package com.example.rewrite.repository.ordercart;

import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderCartRepository extends JpaRepository<OrderCart, Long> {

    List<OrderCart> findByOrders(Orders orders);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderCart oc WHERE oc.product.prodId = :productId")
    void deleteByProductProdId(@Param("productId") Long productId);


    void deleteByProduct(Product product);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderCart oc WHERE oc.orders.orderId = :orderId")
    void deleteByOrdersOrderId(@Param("orderId") Long orderId);
}
