package org.zerock.restqrpayment_2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    private final RestaurantService restaurantService;

    // 1. Read - 식당 목록 조회 (User, Owner, Admin)
    @GetMapping
    public ResponseEntity<PageResponseDTO<RestaurantListAllDTO>> getList(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<RestaurantListAllDTO> responseDTO = restaurantService.listWithAll(pageRequestDTO);
        return ResponseEntity.ok(responseDTO); // 200 OK
    }

    // 2. Read - 특정 식당 조회 (Owner는 자기 식당만, Admin은 모든 식당 조회 가능)
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id, PageRequestDTO pageRequestDTO) {
        RestaurantDTO restaurantDTO = restaurantService.readOne(id);

        if (restaurantDTO != null) {
            return ResponseEntity.ok(restaurantDTO); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    // 3. Create - 식당 등록 (Owner는 자기 식당만, Admin은 모든 식당 등록 가능)
    @PostMapping
    public ResponseEntity<?> registerRestaurant(
            @Valid @RequestBody RestaurantDTO restaurantDTO,
            BindingResult bindingResult) {

        // 유효성 검사 실패 처리
        if (bindingResult.hasErrors()) {
            log.info("Validation errors occurred");

            // 에러 메시지 응답
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 등록 시점에 점주 ID 확인
        if (restaurantDTO.getOwnerId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Owner ID is required.");
        }

        Long registeredId = restaurantService.register(restaurantDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredId); // 201 Created
    }

    // 4. Update - 식당 수정 (Owner는 자기 식당만, Admin은 모든 식당 수정 가능)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@Valid @RequestBody RestaurantDTO restaurantDTO,
                                              BindingResult bindingResult) {

        // 유효성 검사 실패 처리
        if (bindingResult.hasErrors()) {
            log.info("Validation errors occurred");

            // 에러 메시지 응답 (HTTP 400 Bad Request)
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 수정 로직 처리
        restaurantService.modify(restaurantDTO);

        // 성공 응답 (HTTP 200 OK)
        return ResponseEntity.ok().body("Board modified successfully.");
    }

    // 5. Delete - 식당 삭제 (Admin만 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        Long id = restaurantDTO.getId();

        restaurantService.remove(id);

        //게시물이 삭제되었다면 첨부 파일 삭제
        log.info(restaurantDTO.getFileNames());
        List<String> fileNames = restaurantDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        return ResponseEntity.noContent().build(); // 204 No Content
    }


    public void removeFiles(List<String> files){

        for (String fileName:files) {

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();


            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }//end for
    }
}
