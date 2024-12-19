package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerRestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/restaurants")
    @ResponseBody
    public ResponseEntity<List<RestaurantDTO>> getRestaurants(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String ownerId = userDetails.getUsername();
            log.info("Fetching restaurants for owner: " + ownerId);
            List<RestaurantDTO> restaurants = restaurantService.getRestaurantsByOwnerId(ownerId);
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            log.error("Error fetching restaurants: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<RestaurantDTO> getRestaurant(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        RestaurantDTO restaurantDTO = restaurantService.readOne(id);
        
        // 권한 체크
        if (!restaurantDTO.getOwnerId().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(restaurantDTO);
    }

    @PostMapping("/restaurants")
    @ResponseBody
    public ResponseEntity<Long> createRestaurant(
            @RequestBody RestaurantDTO restaurantDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Restaurant DTO: " + restaurantDTO);
        
        // category 값이 전달되는지 확인
        if (restaurantDTO.getCategory() == null || restaurantDTO.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        restaurantDTO.setOwnerId(userDetails.getUsername());
        
        Long id = restaurantService.register(restaurantDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDTO restaurantDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        restaurantDTO.setId(id);
        restaurantDTO.setOwnerId(userDetails.getUsername());
        restaurantService.modify(restaurantDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.remove(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String ownerId = userDetails.getUsername();
        
        try {
            // 현재 로그인한 사용자의 레스토랑만 조회
            List<RestaurantDTO> restaurants = restaurantService.getRestaurantsByOwnerId(ownerId);
            model.addAttribute("restaurants", restaurants);
            return "owner/owner-dashboard";
        } catch (Exception e) {
            log.error("Error fetching restaurants: ", e);
            return "error/500";
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getRestaurantStats(@PathVariable Long id) {
        // 현재는 더미 데이터 반환
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayOrders", 0);
        stats.put("totalSales", 0);
        stats.put("averageRating", 0.0);
        
        return ResponseEntity.ok(stats);
    }
}
