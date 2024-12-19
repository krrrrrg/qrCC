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
import java.util.stream.Collectors;

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
            log.info("Found restaurant: {}", restaurant);
            log.info("Restaurant description: {}", restaurant != null ? restaurant.getDescription() : "null");
            
            // 메뉴 목록 조회
            List<MenuDTO> menus = menuService.getMenusByRestaurant(restaurantId);
            log.info("Found {} menus for restaurant {}", menus.size(), restaurantId);
            
            // 메뉴 카테고리 추출
            List<String> categories = menus.stream()
                .map(MenuDTO::getMenuCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            log.info("Categories found: {}", categories);

            // 모델에 데이터 추가
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("categories", categories);
            model.addAttribute("menus", menus);
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("tableId", tableId);

            return "common/index";
        } catch (Exception e) {
            log.error("Error loading restaurant page", e);
            return "error/404";
        }
    }
}
