package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.service.MenuService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/owner/menus")
@Log4j2
@RequiredArgsConstructor
public class OwnerMenuController {

    private final MenuService menuService;

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuDTO>> getMenusByRestaurantId(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String ownerId = userDetails.getUsername();
        List<MenuDTO> menus = menuService.getMenusByRestaurantAndOwner(restaurantId, ownerId);
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> display(@RequestParam(required = true) String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                log.warn("Empty file name provided");
                return ResponseEntity.badRequest().build();
            }

            log.info("Displaying image: {}", fileName);
            String filePath = uploadPath + File.separator + fileName;
            
            // 파일 경로 로깅
            log.info("Full file path: {}", filePath);
            
            File imageFile = new File(filePath);
            
            if(!imageFile.exists() || !imageFile.isFile()) {
                log.warn("Image file not found or is not a file: {}", fileName);
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String contentType = Files.probeContentType(imageFile.toPath());
            
            if(contentType == null) {
                contentType = "image/jpeg";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(imageBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
        } catch(Exception e) {
            log.error("Error displaying image: {} - {}", fileName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
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
