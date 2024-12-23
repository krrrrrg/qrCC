package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.service.MenuService;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class HomeController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    @GetMapping("/menu")
    public String home(@RequestParam Long restaurantId, 
                      @RequestParam Long tableId, 
                      Model model) {
        try {
            // 레스토랑 정보 조회
            RestaurantDTO restaurant = restaurantService.readOne(restaurantId);
            if (restaurant == null) {
                return "redirect:/error";
            }
            model.addAttribute("restaurant", restaurant);
            
            // 메뉴 목록 조회
            List<MenuDTO> menus = menuService.getMenusByRestaurant(restaurantId);
            model.addAttribute("menus", menus);
            model.addAttribute("tableId", tableId);
            
            return "common/index";
        } catch (Exception e) {
            log.error("메뉴 페이지 로드 중 에러 발생", e);
            return "redirect:/error";
        }
    }
}
