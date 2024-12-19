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
                    return ResponseEntity.ok(menus);
                }
            }
            // 일반 사용자나 비인증 사용자의 경우
            List<MenuDTO> menus = menuService.getMenusByRestaurant(restaurantId);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("메뉴 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메뉴 목록을 불러오는데 실패했습니다.");
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
            
            MenuDTO menuDTO = menus.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);

            if (menuDTO != null) {
                return ResponseEntity.ok(menuDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("메뉴를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("메뉴 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메뉴를 불러오는데 실패했습니다.");
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
            
            Set<String> categories = menus.stream()
                .map(MenuDTO::getMenuCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
            
            return ResponseEntity.ok(new ArrayList<>(categories));
        } catch (Exception e) {
            log.error("카테고리 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("카테고리 목록을 불러오는데 실패했습니다.");
        }
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
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @RequestPart("menuData") @Valid MenuDTO menuDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(errorMessage);
            }

            menuDTO.setId(id);
            menuDTO.setRestaurantId(restaurantId);

            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file);
                menuDTO.setFileNames(Collections.singletonList(fileName));
            }

            MenuDTO updatedMenu = menuService.updateMenu(id, menuDTO, userDetails.getUsername());
            return ResponseEntity.ok(updatedMenu);
        } catch (Exception e) {
            log.error("메뉴 수정 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메뉴 수정에 실패했습니다: " + e.getMessage());
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
            return ResponseEntity.ok().body("메뉴가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("메뉴 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메뉴 삭제에 실패했습니다: " + e.getMessage());
        }
    }

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
}
