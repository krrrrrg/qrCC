package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user/order")
@RequiredArgsConstructor
@Log4j2
public class OrderViewController {

    @GetMapping("/history")
    public String orderHistory(
            @RequestParam Long restaurantId,
            @RequestParam Long tableId,
            Model model) {
        log.info("Order history for restaurant: {}, table: {}", restaurantId, tableId);
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("tableId", tableId);
        return "user/order-history";
    }

    @GetMapping("/status")
    public String orderStatus(
            @RequestParam Long orderId,
            @RequestParam Long restaurantId,
            @RequestParam Long tableId,
            Model model) {
        log.info("Order status for order: {}, restaurant: {}, table: {}", orderId, restaurantId, tableId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("tableId", tableId);
        return "user/order-status";
    }
}
