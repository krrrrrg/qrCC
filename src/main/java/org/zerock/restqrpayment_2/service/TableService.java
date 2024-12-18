package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.RestaurantTable;
import org.zerock.restqrpayment_2.repository.TableRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TableService {

    private final TableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public RestaurantTable createTable(Long restaurantId) {
        int currentTableCount = tableRepository.countByRestaurantId(restaurantId);
        
        RestaurantTable table = RestaurantTable.builder()
                .restaurant(restaurantRepository.findById(restaurantId).orElseThrow())
                .tableNumber(currentTableCount + 1)
                .status("AVAILABLE")
                .build();
                
        return tableRepository.save(table);
    }

    public void deleteTable(Long tableId) {
        tableRepository.deleteById(tableId);
    }

    public String generateQRCode(Long tableId) {
        RestaurantTable table = tableRepository.findById(tableId).orElseThrow();
        return "Generated QR Code";
    }
} 