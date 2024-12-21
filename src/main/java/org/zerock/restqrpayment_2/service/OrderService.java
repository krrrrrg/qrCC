package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.*;
import org.zerock.restqrpayment_2.dto.OrderDTO;
import org.zerock.restqrpayment_2.repository.OrderRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;
import org.zerock.restqrpayment_2.repository.RestaurantTableRepository;
import org.zerock.restqrpayment_2.repository.MenuRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        log.info("Creating order from DTO: {}", orderDTO);

        // 레스토랑과 테이블 조회
        Restaurant restaurant = restaurantRepository.findById(orderDTO.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        RestaurantTable table = tableRepository.findById(orderDTO.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // 주문 생성
        Order order = Order.builder()
                .restaurant(restaurant)
                .table(table)
                .status(Order.OrderStatus.PENDING)
                .build();

        // 주문 아이템 추가
        orderDTO.getItems().forEach(itemDTO -> {
            Menu menu = menuRepository.findById(itemDTO.getMenuId())
                    .orElseThrow(() -> new RuntimeException("Menu not found"));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(itemDTO.getQuantity())
                    .price((int)(menu.getPrice() * itemDTO.getQuantity()))
                    .build();

            order.addOrderItem(orderItem);
        });

        // 총액 계산
        order.updateTotalAmount();

        // 저장
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
