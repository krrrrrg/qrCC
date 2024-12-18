package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.CreateTableRequest;
import org.zerock.restqrpayment_2.service.TableService;
import org.zerock.restqrpayment_2.domain.RestaurantTable;
import org.zerock.restqrpayment_2.repository.TableRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;
    private final TableRepository tableRepository;

    @PostMapping
    public ResponseEntity<?> createTable(
            @RequestBody CreateTableRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            RestaurantTable table = tableService.createTable(request.getRestaurantId());
            return ResponseEntity.ok(table);
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

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<RestaurantTable>> getTablesByRestaurant(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<RestaurantTable> tables = tableService.getTablesByRestaurant(restaurantId, userDetails.getUsername());
            return ResponseEntity.ok(tables);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 