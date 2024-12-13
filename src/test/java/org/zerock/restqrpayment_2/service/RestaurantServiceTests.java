package org.zerock.restqrpayment_2.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.restqrpayment_2.dto.*;

import java.util.List;

@SpringBootTest
@Log4j2
public class RestaurantServiceTests {

    @Autowired
    private RestaurantService restaurantService;

    @Test
    public void testRegister() {
        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .name("new restaurant")
                .address("new address")
                .category("korean food")
                .phoneNumber("010-1234-1234")
                .build();

        Long id = restaurantService.register(restaurantDTO);

        log.info(id);
    }

    @Test
    public void testModify() {
        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .id(101L)
                .name("updated restaurant")
                .address("updated address")
                .category("korean food")
                .phoneNumber("010-1234-1234")
                .refLink("updated ref link")
                .description("good korean food")
                .build();

        restaurantService.modify(restaurantDTO);
    }

    @Test
    public void testListWithAll() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<RestaurantListAllDTO> responseDTO = restaurantService.listWithAll(pageRequestDTO);

        List<RestaurantListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(restaurantListAllDTO -> {
            log.info(restaurantListAllDTO.getId() + ":" + restaurantListAllDTO.getName());

            if(restaurantListAllDTO.getRestaurantImages() != null) {
                for (RestaurantImageDTO restaurantImageDTO : restaurantListAllDTO.getRestaurantImages() ){
                    log.info(restaurantImageDTO);
                }
            }

            log.info("--------------------------------------");
        });
    }

}
