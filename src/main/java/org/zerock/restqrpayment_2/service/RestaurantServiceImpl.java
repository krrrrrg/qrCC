package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    @Override
    public RestaurantDTO readOne(Long id) {
        Optional<Restaurant> result = restaurantRepository.findByIdWithImages(id);

        Restaurant restaurant = result.orElseThrow();

        RestaurantDTO restaurantDTO = entityToDTO(restaurant);

        log.info(restaurantDTO);

        return restaurantDTO;
    }

    @Override
    public Long register(RestaurantDTO restaurantDTO) {
        log.info("Restaurant DTO: " + restaurantDTO);
        
        Restaurant restaurant = restaurantDTO.toEntity();
        restaurant.setBusinessType(restaurantDTO.getCategory());
        
        Restaurant saved = restaurantRepository.save(restaurant);
        return saved.getId();
    }

    @Override
    public void modify(RestaurantDTO restaurantDTO) {
        Optional<Restaurant> result = restaurantRepository.findById(restaurantDTO.getId());

        Restaurant restaurant = result.orElseThrow();

        restaurant.changeRestaurant(
                restaurantDTO.getName(),
                restaurantDTO.getAddress(),
                restaurantDTO.getCategory(),
                restaurantDTO.getPhoneNumber(),
                restaurantDTO.getDescription(),
                restaurantDTO.getRefLink()
        );

        restaurant.clearRestaurantImages();

        if(restaurantDTO.getFileNames() != null) {
            for(String fileName : restaurantDTO.getFileNames()) {

                String[] arr = fileName.split("_");
                restaurant.addRestaurantImage(arr[0], arr[1]);
            }
        }

        restaurantRepository.save(restaurant);
    }

    @Override
    public void remove(Long id) {
        restaurantRepository.deleteById(id);
    }


    @Override
    public PageResponseDTO<RestaurantListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<RestaurantListAllDTO> result = restaurantRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<RestaurantListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public List<RestaurantDTO> getRestaurantsByOwnerId(String ownerId) {
        log.info("Getting restaurants for owner: " + ownerId);
        
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);
        return restaurants.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantDTO entityToDTO(Restaurant restaurant) {
        return RestaurantDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .category(restaurant.getCategory())
                .phoneNumber(restaurant.getPhoneNumber())
                .description(restaurant.getDescription())
                .refLink(restaurant.getRefLink())
                .ownerId(restaurant.getOwnerId())
                .fileNames(restaurant.getImageSet().stream()
                        .map(restaurantImage -> restaurantImage.getUuid() + "_" + restaurantImage.getFileName())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Restaurant dtoToEntity(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantDTO.getId())
                .name(restaurantDTO.getName())
                .address(restaurantDTO.getAddress())
                .category(restaurantDTO.getCategory())
                .phoneNumber(restaurantDTO.getPhoneNumber())
                .description(restaurantDTO.getDescription())
                .refLink(restaurantDTO.getRefLink())
                .ownerId(restaurantDTO.getOwnerId())
                .build();

        if (restaurantDTO.getFileNames() != null) {
            restaurantDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                restaurant.addRestaurantImage(arr[0], arr[1]);
            });
        }

        return restaurant;
    }

}
