package com.qrservice.service;

import com.qrservice.model.Restaurant;
import com.qrservice.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> getRestaurantsByOwnerId(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Restaurant restaurant) {
        Restaurant existingRestaurant = getRestaurantById(restaurant.getId());
        
        // 권한 체크
        if (!existingRestaurant.getOwnerId().equals(restaurant.getOwnerId())) {
            throw new AccessDeniedException("You don't have permission to update this restaurant");
        }

        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Long id, String ownerId) {
        Restaurant restaurant = getRestaurantById(id);
        
        // 권한 체크
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You don't have permission to delete this restaurant");
        }

        restaurantRepository.deleteById(id);
    }
}
