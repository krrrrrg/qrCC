package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.zerock.restqrpayment_2.domain.RestaurantTable;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.repository.TableRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class TableService {

    private final TableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public boolean isRestaurantOwner(Long restaurantId, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("레스토랑을 찾을 수 없습니다."));
        return restaurant.getOwnerId().equals(ownerId);
    }

    public RestaurantTable createTable(Long restaurantId) {
        if (!isRestaurantOwner(restaurantId, restaurantRepository.findById(restaurantId).orElseThrow().getOwnerId())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
            
        int currentTableCount = tableRepository.countByRestaurantId(restaurantId);
        
        RestaurantTable table = RestaurantTable.builder()
                .restaurant(restaurantRepository.findById(restaurantId).orElseThrow())
                .tableNumber(currentTableCount + 1)
                .status("AVAILABLE")
                .build();
                
        return tableRepository.save(table);
    }

    public List<RestaurantTable> getTablesByRestaurant(Long restaurantId, String ownerId) {
        if (!isRestaurantOwner(restaurantId, ownerId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        
        return tableRepository.findByRestaurantId(restaurantId);
    }

    public void deleteTable(Long tableId, String ownerId) {
        RestaurantTable table = tableRepository.findById(tableId)
            .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));
            
        if (!isRestaurantOwner(table.getRestaurant().getId(), ownerId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        
        tableRepository.deleteById(tableId);
    }

    public Map<String, String> generateQRCode(Long tableId, String ownerId) {
        RestaurantTable table = tableRepository.findById(tableId)
            .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));
        
        Restaurant restaurant = table.getRestaurant();
        
        if (!isRestaurantOwner(restaurant.getId(), ownerId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        
        String url = String.format("http://localhost:8090/table?id=%d&restaurantId=%d", 
            table.getId(), restaurant.getId());
            
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("tableNumber", table.getTableNumber().toString());
        response.put("restaurantName", restaurant.getName());
        
        return response;
    }
} 