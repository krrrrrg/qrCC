package org.zerock.restqrpayment_2.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;
import org.zerock.restqrpayment_2.service.RestaurantService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getRestaurantsByOwnerId(String ownerId) {
        log.info("Getting restaurants for owner: " + ownerId);
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);
        return restaurants.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDTO readOne(Long id) {
        log.info("Reading restaurant with id: {}", id);
        Optional<Restaurant> result = restaurantRepository.findById(id);
        Restaurant restaurant = result.orElseThrow();
        log.info("Found restaurant entity: {}", restaurant);
        RestaurantDTO dto = entityToDTO(restaurant);
        log.info("Converted to DTO: {}", dto);
        return dto;
    }

    @Override
    public Long register(RestaurantDTO restaurantDTO) {
        log.info("Restaurant DTO: " + restaurantDTO);
        Restaurant restaurant = dtoToEntity(restaurantDTO);
        log.info("Restaurant Entity: " + restaurant);
        Restaurant saved = restaurantRepository.save(restaurant);
        return saved.getId();
    }

    @Override
    public void modify(RestaurantDTO restaurantDTO) {
        Optional<Restaurant> result = restaurantRepository.findById(restaurantDTO.getId());
        Restaurant restaurant = result.orElseThrow();
        restaurant.change(restaurantDTO.getName(), restaurantDTO.getAddress(), 
                        restaurantDTO.getPhoneNumber(), restaurantDTO.getDescription(),
                        restaurantDTO.getCategory());
        restaurantRepository.save(restaurant);
    }

    @Override
    public void remove(Long id) {
        restaurantRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<RestaurantListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        // 임시로 빈 응답 반환
        return PageResponseDTO.<RestaurantListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(List.of())
                .total(0L)
                .build();
    }

    @Override
    public RestaurantDTO entityToDTO(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantDTO.class);
    }

    @Override
    public Restaurant dtoToEntity(RestaurantDTO restaurantDTO) {
        return modelMapper.map(restaurantDTO, Restaurant.class);
    }
} 