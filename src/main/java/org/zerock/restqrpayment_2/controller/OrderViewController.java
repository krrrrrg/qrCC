package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.restqrpayment_2.dto.OrderDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.service.OrderService;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;
    private final RestaurantService restaurantService;

    @GetMapping("/order-status")
    public String orderStatus(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("tableId") Long tableId,
            @RequestParam("orderId") Long orderId,
            Model model) {
        log.info("=== Order Status Page Request ===");
        log.info("Restaurant ID: {}", restaurantId);
        log.info("Table ID: {}", tableId);
        log.info("Order ID: {}", orderId);
        
        try {
            // 주문 정보 가져오기
            OrderDTO orderDTO = orderService.getOrder(orderId);
            log.info("Order DTO: {}", orderDTO);
            if (orderDTO == null) {
                log.warn("Order not found for ID: {}", orderId);
                return "redirect:/error";
            }
            
            // 레스토랑 정보 가져오기
            RestaurantDTO restaurantDTO = restaurantService.getRestaurant(restaurantId);
            log.info("Restaurant DTO: {}", restaurantDTO);
            if (restaurantDTO == null) {
                log.warn("Restaurant not found for ID: {}", restaurantId);
                return "redirect:/error";
            }
            
            model.addAttribute("order", orderDTO);
            model.addAttribute("restaurant", restaurantDTO);
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("tableId", tableId);
            
            log.info("=== Returning order-status view ===");
            return "user/order-status";
        } catch (Exception e) {
            log.error("Error getting order status: ", e);
            return "redirect:/error";
        }
    }

    @GetMapping("/user/order-history")
    public String orderHistory(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("tableId") Long tableId,
            Model model) {
        try {
            List<OrderDTO> orders = orderService.getOrderHistory(restaurantId, tableId);
            model.addAttribute("orders", orders);
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("tableId", tableId);
            return "user/order-history";
        } catch (Exception e) {
            log.error("Error getting order history: ", e);
            return "redirect:/error";
        }
    }
}
