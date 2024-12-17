package com.qrservice.controller;

import com.qrservice.model.Restaurant;
import com.qrservice.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/owner")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // 레스토랑 목록 조회
    @GetMapping("/restaurants")
    @ResponseBody
    public List<Restaurant> getRestaurants(@AuthenticationPrincipal UserDetails userDetails) {
        return restaurantService.getRestaurantsByOwnerId(userDetails.getUsername());
    }

    // 특정 레스토랑 조회
    @GetMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    // 레스토랑 생성
    @PostMapping("/restaurants")
    @ResponseBody
    public ResponseEntity<Restaurant> createRestaurant(
            @RequestBody Restaurant restaurant,
            @AuthenticationPrincipal UserDetails userDetails) {
        restaurant.setOwnerId(userDetails.getUsername());
        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);
        return ResponseEntity.ok(savedRestaurant);
    }

    // 레스토랑 수정
    @PutMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant,
            @AuthenticationPrincipal UserDetails userDetails) {
        restaurant.setId(id);
        restaurant.setOwnerId(userDetails.getUsername());
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurant);
        return ResponseEntity.ok(updatedRestaurant);
    }

    // 레스토랑 삭제
    @DeleteMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteRestaurant(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        restaurantService.deleteRestaurant(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 대시보드에 레스토랑 목록 전달
    @GetMapping("/dashboard")
    public String getDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByOwnerId(userDetails.getUsername());
        model.addAttribute("restaurants", restaurants);
        return "owner/owner-dashboard";
    }
}
