package org.zerock.restqrpayment_2.service;

import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

public interface RestaurantService {
    RestaurantDTO readOne(Long id);
    Long register(RestaurantDTO restaurantDTO);
    void modify(RestaurantDTO restaurantDTO);
    void remove(Long id);
    PageResponseDTO<RestaurantListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);
    List<RestaurantDTO> getRestaurantsByOwnerId(String ownerId);
    RestaurantDTO entityToDTO(Restaurant restaurant);
    Restaurant dtoToEntity(RestaurantDTO restaurantDTO);
}
