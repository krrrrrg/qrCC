package org.zerock.restqrpayment_2.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.restqrpayment_2.domain.*;
import org.zerock.restqrpayment_2.dto.OrderDTO;
import org.zerock.restqrpayment_2.dto.OrderItemDTO;
import org.zerock.restqrpayment_2.repository.MenuRepository;
import org.zerock.restqrpayment_2.repository.OrderRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;
import org.zerock.restqrpayment_2.repository.RestaurantTableRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuRepository menuRepository;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        // 기본 데이터 검증
        validateOrderData(orderDTO);
        
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

        // 주문 항목 추가 및 검증
        orderDTO.getOrderItems().forEach(item -> {
            validateOrderItem(item);
            Menu menu = menuRepository.findById(item.getMenuId())
                    .orElseThrow(() -> new RuntimeException("Menu not found"));

            // 메뉴 가격 검증
            validateMenuPrice(menu, item.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(item.getQuantity())
                    .price((int)(menu.getPrice().doubleValue() * item.getQuantity()))
                    .build();

            order.addOrderItem(orderItem);
        });

        // 총 금액 계산
        order.updateTotalAmount();

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        return entityToDto(savedOrder);
    }

    private void validateOrderData(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("주문 데이터가 없습니다.");
        }
        if (orderDTO.getRestaurantId() == null) {
            throw new IllegalArgumentException("레스토랑 정보가 없습니다.");
        }
        if (orderDTO.getTableId() == null) {
            throw new IllegalArgumentException("테이블 정보가 없습니다.");
        }
        if (orderDTO.getOrderItems() == null || orderDTO.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 없습니다.");
        }
    }

    private void validateOrderItem(OrderItemDTO item) {
        if (item.getMenuId() == null) {
            throw new IllegalArgumentException("메뉴 정보가 없습니다.");
        }
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new IllegalArgumentException("올바르지 않은 주문 수량입니다.");
        }
    }

    private void validateMenuPrice(Menu menu, int quantity) {
        if (menu.getPrice() <= 0) {
            throw new IllegalArgumentException("올바르지 않은 메뉴 가격입니다.");
        }
        
        // Double을 long으로 변환하여 계산
        long totalPrice = (long)(menu.getPrice().doubleValue() * quantity);
        if (totalPrice > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("주문 금액이 너무 큽니다.");
        }
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.updateStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return entityToDto(savedOrder);
    }

    @Transactional
    public List<OrderDTO> getOrders(Long tableId, String status) {
        List<Order> orders;
        
        if (tableId != null && status != null) {
            orders = orderRepository.findByTableIdAndStatus(
                tableId, Order.OrderStatus.valueOf(status));
        } else if (tableId != null) {
            orders = orderRepository.findByTableId(tableId);
        } else if (status != null) {
            orders = orderRepository.findByStatus(Order.OrderStatus.valueOf(status));
        } else {
            orders = orderRepository.findAll();
        }
        
        return orders.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return entityToDto(order);
    }

    @Transactional
    public List<OrderDTO> getOrderHistory(Long restaurantId, Long tableId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        List<Order> orders = orderRepository.findByRestaurantAndTableOrderByRegDateDesc(restaurant, table);
        return orders.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    private OrderDTO entityToDto(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurant().getId())
                .tableId(order.getTable().getId())
                .restaurantName(order.getRestaurant().getName())
                .tableNumber(order.getTable().getTableNumber())
                .orderItems(order.getOrderItems().stream()
                        .map(this::orderItemToDto)
                        .collect(Collectors.toList()))
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .regDate(order.getRegDate())
                .modDate(order.getModDate())
                .build();
    }

    private OrderItemDTO orderItemToDto(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .menuId(orderItem.getMenu().getId())
                .menuName(orderItem.getMenu().getName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
