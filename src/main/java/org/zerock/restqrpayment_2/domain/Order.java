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
        PENDING,     // 주문 대기
        ACCEPTED,    // 주문 접수
        COMPLETED,   // 완료
        CANCELLED    // 취소됨
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
    @Column(nullable = false)
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

    public void updateStatus(OrderStatus newStatus) {
        validateStatusTransition(this.status, newStatus);
        this.status = newStatus;
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("취소된 주문은 상태를 변경할 수 없습니다.");
        }
        
        if (currentStatus == OrderStatus.COMPLETED) {
            throw new IllegalStateException("완료된 주문은 상태를 변경할 수 없습니다.");
        }

        // PENDING -> ACCEPTED -> COMPLETED 순서로만 진행 가능
        if (currentStatus == OrderStatus.PENDING && newStatus != OrderStatus.ACCEPTED 
            && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException("대기 중인 주문은 접수 또는 취소만 가능합니다.");
        }

        if (currentStatus == OrderStatus.ACCEPTED && newStatus != OrderStatus.COMPLETED 
            && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException("접수된 주문은 완료 또는 취소만 가능합니다.");
        }
    }
}
