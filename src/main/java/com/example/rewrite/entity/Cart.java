package com.example.rewrite.entity;

import lombok.*;
import javax.persistence.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "cart",
        uniqueConstraints = @UniqueConstraint(columnNames = {"uid", "prod_id"})
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId; // 단일 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id", referencedColumnName = "prod_id", nullable = false)
    private Product product;

    @Builder.Default
    @Column(name = "is_checked",  columnDefinition = "boolean default false")
    private Boolean isChecked = false; //checked

}