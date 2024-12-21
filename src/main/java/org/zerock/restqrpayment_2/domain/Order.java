package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"restaurant", "table", "orderItems"})
public class Order extends BaseEntity {
    
    public enum OrderStatus {
        PENDING,    // 주문 대기
        ACCEPTED,   // 주문 접수
        COMPLETED,  // 완료
        CANCELLED   // 취소
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;
    
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private Integer totalAmount;
    
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    
    public void updateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .mapToInt(item -> (int)(item.getMenu().getPrice() * item.getQuantity()))
                .sum();
    }
}
