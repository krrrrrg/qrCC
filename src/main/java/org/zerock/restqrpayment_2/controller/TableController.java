package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.CreateTableRequest;
import org.zerock.restqrpayment_2.dto.TableDTO;
import org.zerock.restqrpayment_2.service.TableService;
import org.zerock.restqrpayment_2.domain.RestaurantTable;
import org.zerock.restqrpayment_2.repository.TableRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;
    private final TableRepository tableRepository;

    private TableDTO convertToDTO(RestaurantTable table) {
        return TableDTO.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .status(table.getStatus())
                .restaurantId(table.getRestaurant().getId())
                .restaurantName(table.getRestaurant().getName())
                .build();
    }

    @PostMapping
    public ResponseEntity<?> createTable(
            @Valid @RequestBody CreateTableRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 레스토랑 소유자 확인
            if (!tableService.isRestaurantOwner(request.getRestaurantId(), userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("해당 레스토랑에 대한 권한이 없습니다.");
            }
            
            RestaurantTable table = tableService.createTable(request.getRestaurantId());
            return ResponseEntity.ok(convertToDTO(table));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("테이블 생성 중 오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<TableDTO>> getTablesByRestaurant(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<RestaurantTable> tables = tableService.getTablesByRestaurant(restaurantId, userDetails.getUsername());
            List<TableDTO> tableDTOs = tables.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tableDTOs);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTable(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        tableService.deleteTable(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<?> generateQR(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, String> qrData = tableService.generateQRCode(id, userDetails.getUsername());
        return ResponseEntity.ok(qrData);
    }
}