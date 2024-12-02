package org.zerock.restqrpayment_2.service;


import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface RestaurantService {

    RestaurantDTO readOne(Long id);

    Long register(RestaurantDTO restaurantDTO);

    void modify(RestaurantDTO restaurantDTO);

    void remove(Long id);

    PageResponseDTO<RestaurantListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    default Restaurant dtoToEntity(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantDTO.getId())
                .name(restaurantDTO.getName())
                .address(restaurantDTO.getAddress())
                .category(restaurantDTO.getCategory())
                .phoneNumber(restaurantDTO.getPhoneNumber())
                .refLink(restaurantDTO.getRefLink())
                .description(restaurantDTO.getDescription())
                .ownerId(restaurantDTO.getOwnerId())
                .build();

        if(restaurantDTO.getFileNames() != null) {
            restaurantDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                restaurant.addRestaurantImage(arr[0], arr[1]);
            });
        }

        return restaurant;
    }

    default RestaurantDTO entityToDTO(Restaurant restaurant) {
        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .category(restaurant.getCategory())
                .phoneNumber(restaurant.getPhoneNumber())
                .refLink(restaurant.getRefLink())
                .description(restaurant.getDescription())
                .ownerId(restaurant.getOwnerId())
                .build();

        List<String> fileNames = restaurant.getImageSet().stream().sorted().map(boardImage ->
                boardImage.getUuid()+"_"+boardImage.getFileName()).collect(Collectors.toList());

        restaurantDTO.setFileNames(fileNames);

        return restaurantDTO;
    }

}

