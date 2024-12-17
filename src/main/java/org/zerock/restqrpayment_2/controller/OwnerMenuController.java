package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/owner/menus")
@Log4j2
@RequiredArgsConstructor
public class OwnerMenuController {

    private final MenuService menuService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuDTO>> getMenusByRestaurantId(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String ownerId = userDetails.getUsername();
        List<MenuDTO> menus = menuService.getMenusByRestaurantAndOwner(restaurantId, ownerId);
        return ResponseEntity.ok(menus);
    }

    @PostMapping
    public ResponseEntity<MenuDTO> createMenu(
            @RequestBody MenuDTO menuDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String ownerId = userDetails.getUsername();
        MenuDTO createdMenu = menuService.createMenu(menuDTO, ownerId);
        return ResponseEntity.ok(createdMenu);
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<MenuDTO> updateMenu(
            @PathVariable Long menuId,
            @RequestBody MenuDTO menuDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String ownerId = userDetails.getUsername();
        MenuDTO updatedMenu = menuService.updateMenu(menuId, menuDTO, ownerId);
        return ResponseEntity.ok(updatedMenu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Long menuId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String ownerId = userDetails.getUsername();
        menuService.deleteMenu(menuId, ownerId);
        return ResponseEntity.ok().build();
    }
}
