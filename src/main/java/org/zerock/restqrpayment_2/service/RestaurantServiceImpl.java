package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

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
        Restaurant restaurant = dtoToEntity(restaurantDTO);

        Long id = restaurantRepository.save(restaurant).getId();

        return id;
    }

    @Override
    public void modify(RestaurantDTO restaurantDTO) {
        Optional<Restaurant> result = restaurantRepository.findById(restaurantDTO.getId());
        Restaurant restaurant = result.orElseThrow();
        restaurant.changeRestaurant(restaurantDTO.getName(),
                restaurantDTO.getAddress(),
                restaurantDTO.getCategory(),
                restaurantDTO.getPhoneNumber(),
                restaurantDTO.getRefLink(),
                restaurantDTO.getDescription());

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


}
