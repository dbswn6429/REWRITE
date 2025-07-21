package com.example.rewrite.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressId", nullable = false, length = 255)
    private long addressId; // 주소 ID (기본키)

    @Column(name = "address", nullable = true, length = 255)
    private String address; // 주소

    @Column(name = "uid", nullable = true, length = 255)
    private Long uid; // 사용자 ID (외래키)

    @Column(name = "addressAlias", nullable = true, length = 255)
    private String addressAlias; // 주소 별칭

    @Column(name = "recipient", nullable = true, length = 255)
    private String recipient; // 수령인

    @Column(name = "createdAt", nullable = true, length = 255)
    private String createdAt; // 등록인

    @Column(name = "isDefault", nullable = true, length = 255)
    private String isDefault; // 기본 주소 여부

    @Column(name = "phoneNum", nullable = true, length = 12)
    private String phoneNum; // 전화번호
    public Address(long addressId, String address, Long uid, String addressAlias,
                   String recipient, String createdAt, String isDefault, String phoneNum) {
        this.addressId = addressId;
        this.address = address;
        this.uid = uid;
        this.addressAlias = addressAlias;
        this.recipient = recipient;
        this.createdAt = createdAt;
        this.isDefault = isDefault;
        this.phoneNum = phoneNum;
    }

    @Column(name = "delChk", nullable = true, length = 2)
    private String delChk; // 삭제 여부

}
