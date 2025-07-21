package com.example.rewrite.service.order;

import com.example.rewrite.command.OrderSummaryDto;
import com.example.rewrite.entity.Cart;
import com.example.rewrite.entity.OrderCart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.entity.Product;
import com.example.rewrite.repository.cart.CartRepository;
import com.example.rewrite.repository.order.OrderRepository;
import com.example.rewrite.repository.ordercart.OrderCartRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private OrderCartRepository orderCartRepository;
    @Autowired
    private CartRepository cartRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Transactional
    @Override
    public void saveOrder(Orders order, List<Cart> checkedCarts) {
        logger.info("저장하기 전 배송 요청 사항: '{}'", order.getDeliveryRequest());
        // 주문 저장
        Orders savedOrder = ordersRepository.save(order);

        logger.info("저장된 주문의 ID: {}, 배송 요청 사항: '{}'",
                savedOrder.getOrderId(), savedOrder.getDeliveryRequest());
        // OrderCart 저장
        List<OrderCart> orderCarts = checkedCarts.stream()
                .map(cart -> OrderCart.builder()
                        .orders(savedOrder)
                        .product(cart.getProduct())
                        .price(Integer.parseInt(cart.getProduct().getPrice()))
                        .build())
                .collect(Collectors.toList());

        orderCartRepository.saveAll(orderCarts);  // OrderCart 목록을 한 번에 저장
    }
    @Transactional
    public void updateOrderWithPaymentInfo(String tossOrderId, String paymentKey,
                                           int amount, String paymentMethod,
                                           String paymentStatus, String approvedAt) {
        // tossOrderId로 주문 찾기
        Orders order = ordersRepository.findByTossOrderId(tossOrderId)
                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));

        // 결제 정보 업데이트
        order.setPaymentKey(paymentKey);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("결제완료"); // 또는 paymentStatus 사용

        try {
            // ISO 8601 형식의 시간 문자열 처리
            LocalDateTime dateTime = LocalDateTime.parse(
                    approvedAt.replace("+09:00", ""),
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            order.setPaidAt(Timestamp.valueOf(dateTime).toLocalDateTime());
        } catch (Exception e) {
            // 시간 변환 오류 시 현재 시간으로 설정
            order.setPaidAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        }

        // 주문 상태 업데이트
        order.setOrderStatus("결제완료");

        // DB에 저장
        ordersRepository.save(order);

        // 필요하다면 여기서 이메일 알림 등 추가 작업 수행
    }

    @Override
    public List<Product> findOrderDetail(Long oid) {
        return ordersRepository.findOrderDetail(oid);
    }

    @Override
    public Orders findByOrderId(Long oid) {
        return ordersRepository.getOrder(oid);
    }

    @Override
    public List<Product> getOrderAll(Long uid) {

        return ordersRepository.getOrderAll(uid);
    }

    @Override
    public List<Product> getOrder4(Long uid) {
        PageRequest pageRequest = PageRequest.of(0, 4); // 첫 번째 페이지, 4개 항목

        return ordersRepository.getOrder4(uid, pageRequest);
    }

    @Override
    public List<OrderSummaryDto> getOrderSummaries(Long uid) {
        return ordersRepository.findOrderSummariesByBuyerUid(uid);
    }
}
