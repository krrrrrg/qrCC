package org.zerock.restqrpayment_2.apiController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.service.MenuService;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MenuController
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@Log4j2
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    // getList와 getMenusByRestaurant를 통합
    @GetMapping
    public ResponseEntity<?> getMenus(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 레스토랑 소유자 검증
            RestaurantDTO restaurantDTO = restaurantService.readOne(restaurantId);
            if (!restaurantDTO.getOwnerId().equals(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("해당 레스토랑의 메뉴를 추회할 권한이 없습니다.");
            }

            List<MenuDTO> menus = menuService.getMenusByRestaurantAndOwner(restaurantId, userDetails.getUsername());
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("메뉴 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메뉴 목록을 불러오는데 실패했습니다.");
        }
    }

    // 2. Read - 특정 메뉴 조회 (User는 모든 메뉴 조회, Owner는 자기 식당 메뉴만, Admin은 모든 메뉴 조회)
    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable("id") Long id) {
        MenuDTO menuDTO = menuService.read(id);

        if (menuDTO != null) {
            return ResponseEntity.ok(menuDTO); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    // 메뉴 카테리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(@PathVariable("restaurantId") Long restaurantId) {
        List<String> categories = menuService.getCategories(restaurantId);
        return ResponseEntity.ok(categories);
    }

    // 3. Create - 메뉴 등록 (Owner는 자기 식당 메뉴만, Admin은 모든 식당 메뉴 등록 가능)
    @PostMapping
    public ResponseEntity<?> registerMenu(
            @PathVariable Long restaurantId,
            @ModelAttribute @Valid MenuDTO menuDTO,
            @RequestPart(value = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(errorMessage);
            }

            menuDTO.setRestaurantId(restaurantId);
            
            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file);
                menuDTO.setFileNames(Collections.singletonList(fileName));
            }

            MenuDTO savedMenu = menuService.createMenu(menuDTO, userDetails.getUsername());
            return ResponseEntity.ok(savedMenu);
        } catch (Exception e) {
            log.error("메뉴 등록 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메뉴 등록에 실패했습니다: " + e.getMessage());
        }
    }

    // 4. Update - 메뉴 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(
            @PathVariable("id") Long id,
            @RequestPart("menuData") @Valid MenuDTO menuDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("Validation errors occurred");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        menuDTO.setId(id);

        // 파일 처리
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = saveFile(file);
                menuDTO.setFileNames(Collections.singletonList(fileName));
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 저장에 실패했습니다.");
            }
        }

        menuService.modify(menuDTO);
        return ResponseEntity.ok().body("Menu modified successfully.");
    }

    // 파일 저장 메서드
    private String saveFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String cleanFileName = originalName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = UUID.randomUUID().toString() + "_" + cleanFileName;
        
        String savePath = System.getProperty("user.home") + "/menu-images/";
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        
        File saveFile = new File(savePath + fileName);
        file.transferTo(saveFile);
        
        return fileName;
    }

    // 5. Delete - 메뉴 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable("id") Long id) {
        try {
            menuService.remove(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("메뉴 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
