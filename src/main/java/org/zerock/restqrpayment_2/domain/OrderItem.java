package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"order", "menu"})
public class OrderItem extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
    
    private Integer quantity;
    
    private Integer price; // 주문 시점의 가격 * 수량
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public void updatePrice() {
        this.price = (int)(this.menu.getPrice() * this.quantity);
    }
}
