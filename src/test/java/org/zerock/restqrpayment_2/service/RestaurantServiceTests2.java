package org.zerock.restqrpayment_2.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.dto.RestaurantDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Log4j2
@Commit
public class RestaurantServiceTests2 {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ModelMapper modelMapper;

    private RestaurantDTO testRestaurant;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void testReadOne() {
        // Given
        Long id = 103L;

        // When
        RestaurantDTO restaurantDTO = restaurantService.readOne(id);

        // Then
        assertNotNull(restaurantDTO);
        assertThat(restaurantDTO.getId()).isEqualTo(id);
        assertThat(restaurantDTO.getFileNames()).contains("newImage1.jpg", "newImage2.jpg");
        log.info("Fetched Restaurant: {}", restaurantDTO);
    }

    @Test
    public void testRegister() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        // Given
        RestaurantDTO newRestaurant = RestaurantDTO.builder()
                .name("New Restaurant")
                .address("New Address")
                .category("New Category")
                .phoneNumber("010-9876-5432")
                .ownerId("newOwner")
                .description("New Description")
                .fileNames(Arrays.asList(uuid1+"newImage1.jpg", uuid2+"newImage2.jpg"))
                .build();

        // When
        Long id = restaurantService.register(newRestaurant);

        // Then
        assertNotNull(id);
        RestaurantDTO registeredRestaurant = restaurantService.readOne(id);
        assertThat(registeredRestaurant.getName()).isEqualTo("New Restaurant");
        assertThat(registeredRestaurant.getFileNames()).contains("newImage1.jpg", "newImage2.jpg");
        log.info("Registered Restaurant: {}", registeredRestaurant);
    }

    @Test
    public void testModify() {
        // Given
        Long id = testRestaurant.getId();
        RestaurantDTO restaurantDTO = restaurantService.readOne(id);

        restaurantDTO.setName("Updated Name");
        restaurantDTO.setDescription("Updated Description");
        restaurantDTO.setFileNames(Arrays.asList("updatedImage1.jpg", "updatedImage2.jpg"));

        // When
        restaurantService.modify(restaurantDTO);

        // Then
        RestaurantDTO updatedRestaurant = restaurantService.readOne(id);
        assertThat(updatedRestaurant.getName()).isEqualTo("Updated Name");
        assertThat(updatedRestaurant.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedRestaurant.getFileNames()).contains("updatedImage1.jpg", "updatedImage2.jpg");
        log.info("Updated Restaurant: {}", updatedRestaurant);
    }

    @Test
    public void testRemove() {
        // Given
        Long id = testRestaurant.getId();

        // When
        restaurantService.remove(id);

        // Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> restaurantService.readOne(id));
        assertThat(exception.getMessage()).contains("not found");
        log.info("Restaurant with ID {} successfully removed.", id);
    }

    @Test
    public void testListWithAll() {
        // Given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("n") // 검색 타입
                .keyword("Setup") // 테스트 데이터의 키워드
                .page(1)
                .size(10)
                .build();

        // When
        PageResponseDTO<RestaurantListAllDTO> result = restaurantService.listWithAll(pageRequestDTO);

        // Then
        assertNotNull(result);
        assertThat(result.getDtoList()).isNotEmpty();
        log.info("Total Elements: {}", result.getTotal());
        result.getDtoList().forEach(dto -> log.info("Restaurant DTO: {}", dto));
    }
}
