package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.restqrpayment_2.dto.OrderDTO;
import org.zerock.restqrpayment_2.service.OrderService;
import org.zerock.restqrpayment_2.service.RestaurantService;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;
    private final RestaurantService restaurantService;

    @GetMapping("/order-status")
    public String orderStatus(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("tableId") Long tableId,
            @RequestParam("orderId") Long orderId,
            Model model) {
        
        try {
            OrderDTO order = orderService.getOrder(orderId);
            model.addAttribute("order", order);
            model.addAttribute("restaurant", restaurantService.getRestaurant(restaurantId));
            model.addAttribute("tableId", tableId);
            return "user/order-status";
        } catch (Exception e) {
            log.error("주문 상태 조회 실패", e);
            return "redirect:/menu?restaurantId=" + restaurantId + "&tableId=" + tableId;
        }
    }
}
