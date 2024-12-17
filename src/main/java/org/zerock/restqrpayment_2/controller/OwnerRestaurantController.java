package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Controller
@RequestMapping("/api/owner")
@Log4j2
@RequiredArgsConstructor
public class OwnerRestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/restaurants")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<RestaurantListAllDTO>> getRestaurants(
            @AuthenticationPrincipal UserDetails userDetails) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("o")  // owner 검색
                .keyword(userDetails.getUsername())
                .build();
        
        PageResponseDTO<RestaurantListAllDTO> response = restaurantService.listWithAll(pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable Long id) {
        RestaurantDTO restaurantDTO = restaurantService.readOne(id);
        return ResponseEntity.ok(restaurantDTO);
    }

    @PostMapping("/restaurants")
    @ResponseBody
    public ResponseEntity<Long> createRestaurant(
            @RequestBody RestaurantDTO restaurantDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        restaurantDTO.setOwnerId(userDetails.getUsername());
        Long id = restaurantService.register(restaurantDTO);
        return ResponseEntity.ok(id);
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
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("o")  // owner 검색
                .keyword(userDetails.getUsername())
                .build();
        
        PageResponseDTO<RestaurantListAllDTO> response = restaurantService.listWithAll(pageRequestDTO);
        model.addAttribute("restaurants", response.getDtoList());
        return "owner/owner-dashboard";
    }
}
