package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.CreateTableRequest;
import org.zerock.restqrpayment_2.service.TableService;
import org.zerock.restqrpayment_2.domain.RestaurantTable;
import org.zerock.restqrpayment_2.repository.TableRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;
    private final TableRepository tableRepository;

    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody CreateTableRequest request) {
        return ResponseEntity.ok(tableService.createTable(request.getRestaurantId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<?> generateQR(@PathVariable Long id) {
        String qrCode = tableService.generateQRCode(id);
        return ResponseEntity.ok(qrCode);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<RestaurantTable>> getTablesByRestaurant(@PathVariable Long restaurantId) {
        List<RestaurantTable> tables = tableRepository.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(tables);
    }
} 