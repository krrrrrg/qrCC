package org.zerock.restqrpayment_2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.service.MenuService;


@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@Log4j2
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 1. Read - 메뉴 목록 조회 (User, Owner, Admin)
    @GetMapping
    public ResponseEntity<PageResponseDTO<MenuListAllDTO>> getList(@PathVariable("restaurantId") Long restaurantId,
                                                                   PageRequestDTO pageRequestDTO) {
        PageResponseDTO<MenuListAllDTO> responseDTO = menuService.listWithAll(restaurantId, pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO); // 200 OK
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


    // 3. Create - 메뉴 등록 (Owner는 자기 식당 메뉴만, Admin은 모든 식당 메뉴 등록 가능)
    @PostMapping
    public ResponseEntity<?> registerMenu(
            @Valid @RequestBody MenuDTO menuDTO,
            BindingResult bindingResult) {

        // 유효성 검사 실패 처리
        if (bindingResult.hasErrors()) {
            log.info("Validation errors occurred");

            // 에러 메시지 응답
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Long registeredId = menuService.register(menuDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredId); // 201 Created
    }

    // 4. Update - 메뉴 수정 (Owner는 자기 식당 메뉴만, Admin은 모든 식당 메뉴 수정 가능)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(@Valid @RequestBody MenuDTO menuDTO,
                                         BindingResult bindingResult) {

        // 유효성 검사 실패 처리
        if (bindingResult.hasErrors()) {
            log.info("Validation errors occurred");

            // 에러 메시지 응답 (HTTP 400 Bad Request)
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 수정 로직 처리
        menuService.modify(menuDTO);

        // 성공 응답 (HTTP 200 OK)
        return ResponseEntity.ok().body("Board modified successfully.");
    }

    // 5. Delete - 메뉴 삭제 (Admin만 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@RequestBody MenuDTO menuDTO) {
        Long id = menuDTO.getId();

        menuService.remove(id);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
