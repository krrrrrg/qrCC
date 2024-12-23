package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.restqrpayment_2.service.TableService;
import org.zerock.restqrpayment_2.domain.RestaurantTable;

@Controller
@RequiredArgsConstructor
public class TableViewController {

    private final TableService tableService;

    @GetMapping("/table")
    public String viewTable(
            @RequestParam("id") Long tableId,
            @RequestParam("restaurantId") Long restaurantId,
            RedirectAttributes redirectAttributes) {
        try {
            // 테이블 정보 검증
            RestaurantTable table = tableService.getTable(tableId);
            if (!table.getRestaurant().getId().equals(restaurantId)) {
                return "redirect:/error";
            }

            // index.html로 리다이렉트하면서 필요한 파라미터 전달
            return "redirect:/qr?restaurantId=" + restaurantId + "&tableId=" + tableId;
        } catch (Exception e) {
            return "redirect:/error";
        }
    }
}
