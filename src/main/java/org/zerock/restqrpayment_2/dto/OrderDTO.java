package org.zerock.restqrpayment_2.dto;

import lombok.*;
import org.zerock.restqrpayment_2.domain.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long restaurantId;
    private Long tableId;
    private int totalAmount;
    private OrderStatus orderStatus;
    
    @Builder.Default
    private List<OrderItemDTO> items = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long menuId;
        private int quantity;
        private int price;
    }
}
