package org.zerock.restqrpayment_2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.restqrpayment_2.domain.Order;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long restaurantId;
    private Long tableId;
    private String restaurantName;
    private Integer tableNumber;
    private List<OrderItemDTO> orderItems;
    private Order.OrderStatus status;
    private Integer totalAmount;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
