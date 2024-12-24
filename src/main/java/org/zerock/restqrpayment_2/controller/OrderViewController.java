package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.restqrpayment_2.dto.OrderDTO;
import org.zerock.restqrpayment_2.service.OrderService;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.util.List;

@Controller
@RequestMapping("/user/order")
@Log4j2
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;
    private final RestaurantService restaurantService;

    @GetMapping("/status")
    public String orderStatus(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("tableId") Long tableId,
            @RequestParam("orderId") Long orderId,
            Model model) {

        try {
            OrderDTO orderDTO = orderService.getOrder(orderId);
            if (orderDTO == null) {
                return "redirect:/menu?restaurantId=" + restaurantId + "&tableId=" + tableId;
            }
            model.addAttribute("order", orderDTO);
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("tableId", tableId);
            return "user/order-status";
        } catch (Exception e) {
            log.error("주문 상태 조회 실패: {}", e.getMessage(), e);
            return "redirect:/menu?restaurantId=" + restaurantId + "&tableId=" + tableId;
        }
    }

    @GetMapping("/history")
    public String orderHistory(@RequestParam("restaurantId") Long restaurantId,
                             @RequestParam("tableId") Long tableId,
                             Model model) {
        try {
            List<OrderDTO> orders = orderService.getOrderHistory(restaurantId, tableId);
            model.addAttribute("orders", orders);
            model.addAttribute("restaurant", restaurantService.getRestaurant(restaurantId));
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("tableId", tableId);
            return "user/order-history";
        } catch (Exception e) {
            log.error("주문 내역 조회 실패: {}", e.getMessage(), e);
            return "redirect:/menu?restaurantId=" + restaurantId + "&tableId=" + tableId;
        }
    }
}
