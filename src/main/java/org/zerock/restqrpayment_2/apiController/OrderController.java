package org.zerock.restqrpayment_2.apiController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.domain.Order;
import org.zerock.restqrpayment_2.dto.OrderDTO;
import org.zerock.restqrpayment_2.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            log.info("Creating order: {}", orderDTO);
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            log.error("주문 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        try {
            OrderDTO order = orderService.getOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("주문 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(
            @RequestParam Long restaurantId,
            @RequestParam Long tableId) {
        try {
            List<OrderDTO> orders = orderService.getOrderHistory(restaurantId, tableId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("주문 내역 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, Order.OrderStatus.valueOf(newStatus));
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("주문 상태 업데이트 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @RequestParam(required = false) Long tableId,
            @RequestParam(required = false) String status) {
        try {
            List<OrderDTO> orders = orderService.getOrders(tableId, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("주문 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
