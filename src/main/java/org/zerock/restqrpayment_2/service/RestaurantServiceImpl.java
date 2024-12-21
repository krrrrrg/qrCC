package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.*;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = dtoToEntity(restaurantDTO);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return savedRestaurant.getId();
    }

    @Override
    public RestaurantDTO readOne(Long id) {
        Optional<Restaurant> result = restaurantRepository.findById(id);
        Restaurant restaurant = result.orElseThrow();
        return entityToDTO(restaurant);
    }

    @Override
    public void modify(RestaurantDTO restaurantDTO) {
        Optional<Restaurant> result = restaurantRepository.findById(restaurantDTO.getId());
        Restaurant restaurant = result.orElseThrow();
        restaurant.changeName(restaurantDTO.getName());
        restaurant.changeAddress(restaurantDTO.getAddress());
        restaurant.changePhoneNumber(restaurantDTO.getPhoneNumber());
        restaurant.changeDescription(restaurantDTO.getDescription());
        restaurantRepository.save(restaurant);
    }

    @Override
    public void remove(Long id) {
        restaurantRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<RestaurantListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        return null; // TODO: Implement this method
    }

    @Override
    public List<RestaurantDTO> getRestaurantsByOwnerId(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantDTO entityToDTO(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantDTO.class);
    }

    @Override
    public Restaurant dtoToEntity(RestaurantDTO restaurantDTO) {
        return modelMapper.map(restaurantDTO, Restaurant.class);
    }

    @Override
    public RestaurantDTO getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + restaurantId));
        return entityToDTO(restaurant);
    }
}
