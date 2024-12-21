package org.zerock.restqrpayment_2.apiController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.service.FileService;
import org.zerock.restqrpayment_2.service.MenuService;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
    private final FileService fileService;

    // getList와 getMenusByRestaurant를 통합
    @GetMapping
    public ResponseEntity<?> getMenus(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails != null) {
                // 인증된 사용자의 경우 소유자 검증
                RestaurantDTO restaurantDTO = restaurantService.readOne(restaurantId);
                if (restaurantDTO.getOwnerId().equals(userDetails.getUsername())) {
                    List<MenuDTO> menus = menuService.getMenusByRestaurantAndOwner(restaurantId, userDetails.getUsername());
                    menus.forEach(menu -> {
                        if (menu.getMenuCategory() == null || menu.getMenuCategory().trim().isEmpty()) {
                            menu.setMenuCategory("기타");
                        }
                    });
                    return ResponseEntity.ok(menus);
                }
            }
            // 일반 사용자나 비인증 사용자의 경우
            List<MenuDTO> menus = menuService.getMenusByRestaurant(restaurantId);
            menus.forEach(menu -> {
                if (menu.getMenuCategory() == null || menu.getMenuCategory().trim().isEmpty()) {
                    menu.setMenuCategory("기타");
                }
            });
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("메뉴 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "메뉴 목록을 불러오는데 실패했습니다."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenu(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<MenuDTO> menus = userDetails != null ?
                menuService.getMenusByRestaurantAndOwner(restaurantId, userDetails.getUsername()) :
                menuService.getMenusByRestaurant(restaurantId);
            
            menus.forEach(menu -> {
                if (menu.getMenuCategory() == null || menu.getMenuCategory().trim().isEmpty()) {
                    menu.setMenuCategory("기타");
                }
            });
            
            MenuDTO menuDTO = menus.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);

            if (menuDTO != null) {
                return ResponseEntity.ok(menuDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "메뉴를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            log.error("메뉴 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "메뉴를 불러오는데 실패했습니다."));
        }
    }

    // 메뉴 카테리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<MenuDTO> menus = userDetails != null ?
                menuService.getMenusByRestaurantAndOwner(restaurantId, userDetails.getUsername()) :
                menuService.getMenusByRestaurant(restaurantId);
            
            menus.forEach(menu -> {
                if (menu.getMenuCategory() == null || menu.getMenuCategory().trim().isEmpty()) {
                    menu.setMenuCategory("기타");
                }
            });
            
            Set<String> categories = menus.stream()
                .map(MenuDTO::getMenuCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
            
            return ResponseEntity.ok(new ArrayList<>(categories));
        } catch (Exception e) {
            log.error("카테고리 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "카테고리 목록을 불러오는데 실패했습니다."));
        }
    }

    // 3. Create - 메뉴 등록 (Owner는 자기 식당 메뉴만, Admin은 모든 식당 메뉴 등록 가능)
    @PostMapping
    public ResponseEntity<?> createMenu(
            @PathVariable Long restaurantId,
            @RequestParam("name") String name,
            @RequestParam("menuCategory") String menuCategory,
            @RequestParam("price") Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 카테고리가 비어있으면 "기타"로 설정
            String finalCategory = (menuCategory == null || menuCategory.trim().isEmpty()) ? "기타" : menuCategory.trim();
            
            MenuDTO menuDTO = MenuDTO.builder()
                    .name(name)
                    .menuCategory(finalCategory)
                    .price(price)
                    .description(description)
                    .restaurantId(restaurantId)
                    .build();

            // 이미지 파일 처리
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = fileService.uploadFile(file);
                    if (fileName != null) {
                        menuDTO.setFileNames(Collections.singletonList(fileName));
                        log.info("파일 업로드 성공: {}", fileName);
                    }
                } catch (Exception e) {
                    log.error("파일 업로드 실패: ", e);
                }
            }

            MenuDTO savedMenu = menuService.createMenu(menuDTO, userDetails.getUsername());
            return ResponseEntity.ok(savedMenu);
        } catch (Exception e) {
            log.error("메뉴 생성 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 4. Update - 메뉴 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("menuCategory") String menuCategory,
            @RequestParam("price") Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String finalCategory = (menuCategory == null || menuCategory.trim().isEmpty()) ? "기타" : menuCategory.trim();
            
            MenuDTO menuDTO = MenuDTO.builder()
                    .id(id)
                    .name(name)
                    .menuCategory(finalCategory)
                    .price(price)
                    .description(description)
                    .restaurantId(restaurantId)
                    .build();

            // 이미지 파일 처리
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = fileService.uploadFile(file);
                    if (fileName != null) {
                        menuDTO.setFileNames(Collections.singletonList(fileName));
                        log.info("파일 업로드 성공: {}", fileName);
                    }
                } catch (Exception e) {
                    log.error("파일 업로드 실패: ", e);
                }
            }

            MenuDTO updatedMenu = menuService.updateMenu(id, menuDTO, userDetails.getUsername());
            return ResponseEntity.ok(updatedMenu);
        } catch (Exception e) {
            log.error("메뉴 수정 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 5. Delete - 메뉴 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            menuService.deleteMenu(id, userDetails.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("message", "메뉴가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("메뉴 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "메뉴 삭제에 실패했습니다: " + e.getMessage()));
        }
    }
}
