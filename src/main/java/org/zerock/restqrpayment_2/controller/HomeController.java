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

    @GetMapping("/qr")
    public String index(@RequestParam(required = false) Long restaurantId,
                       @RequestParam(required = false) Long tableId) {
        // 파라미터가 있으면 메뉴 페이지로 리다이렉트
        if (restaurantId != null && tableId != null) {
            return "redirect:/menu?restaurantId=" + restaurantId + "&tableId=" + tableId;
        }
        // 파라미터가 없으면 에러 페이지로
        return "redirect:/error";
    }

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
            
            // 메뉴 카테고리 목록 추출
            List<String> categories = menus.stream()
                .map(MenuDTO::getMenuCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
                
            model.addAttribute("menus", menus);
            model.addAttribute("categories", categories);
            model.addAttribute("tableId", tableId);
            
            return "common/index";
        } catch (Exception e) {
            log.error("메뉴 페이지 로드 중 에러 발생", e);
            return "redirect:/error";
        }
    }
}
