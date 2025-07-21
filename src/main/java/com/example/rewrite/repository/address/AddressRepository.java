package com.example.rewrite.repository.address;

import com.example.rewrite.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {

    //주소 삭제 체크
    @Modifying
    @Transactional
    @Query("UPDATE Address a " +
            "SET a.delChk = 'C'" +
            "WHERE a.addressId = :addressId")
    void deleteAddress(Long addressId);

    //기본 주소지 체크
    @Modifying
    @Transactional
    @Query("UPDATE Address a " +
            "SET a.isDefault = CASE " +
            "    WHEN a.addressId = :addressId THEN 'C' " +
            "    WHEN a.uid = :uid AND a.addressId != :addressId THEN 'N' " +
            "    ELSE a.isDefault " +
            "END " +
            "WHERE a.uid = :uid")
    void checkDefault(Long addressId, Long uid);

    //주소지 조회
    @Query("SELECT a FROM Address a " +
            "WHERE a.uid = :uid " +
            "AND a.delChk !='C'" +
            "OR a.delChk IS NULL " + //delChk가 C 가 아닌 것만 조회 //C일경우 삭제상태인것
            "ORDER BY a.isDefault")
    List<Address> getAddress(@Param("uid") Long uid);

    //주소업데이트 페이지에 출력
    Address findByAddressId(Long addressId);

    //주소지 수정
    @Modifying
    @Transactional
    @Query("UPDATE Address a " +
            "SET a.addressAlias = :#{#address.addressAlias}, " +
            "a.createdAt = :#{#address.createdAt}, " +
            "a.address = :#{#address.address}, " +
            "a.phoneNum = :#{#address.phoneNum} " +
            "WHERE a.addressId = :#{#address.addressId}")
    void modifyAddress(@Param("address") Address address);

    @Modifying // 데이터 변경 쿼리임을 명시
    @Transactional // 삭제 작업은 트랜잭션 내에서 수행
    @Query("DELETE FROM Address a WHERE a.uid = :userId")
    void deleteByUserUid(@Param("userId") Long userId); // 구현 추가
}
