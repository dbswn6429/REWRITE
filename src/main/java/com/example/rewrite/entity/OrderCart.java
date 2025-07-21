package com.example.rewrite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders_cart")
@Builder
public class OrderCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordercart_id")
    private Long orderCartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "prod_id",nullable = false)
    private Product product;

    @Column(name = "price", nullable = false)
    private int price;

}
