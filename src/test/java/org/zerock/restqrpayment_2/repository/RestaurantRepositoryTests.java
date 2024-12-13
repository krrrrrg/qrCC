package org.zerock.restqrpayment_2.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.domain.RestaurantImage;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Log4j2
@SpringBootTest
public class RestaurantRepositoryTests {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Restaurant restaurant = Restaurant.builder()
                    .name("Name " + i)
                    .address("Address " + i)
                    .category("Category " + i)
                    .phoneNumber("Phone " + i)
                    .build();

            Restaurant savedRestaurant = restaurantRepository.save(restaurant);
            log.info(savedRestaurant.getId());
        });


    }

    @Test
    public void testSelect() {
        Long id = 99L;

        Optional<Restaurant> result = restaurantRepository.findById(id);

        Restaurant restaurant = result.orElseThrow();

        log.info(restaurant);
    }

    @Test
    public void testUpdate () {
        Long id = 100L;

        Optional<Restaurant> result = restaurantRepository.findById(id);

        Restaurant restaurant = result.orElseThrow();

        restaurant.changeRestaurant("updated name","updated address", "updated category",  "010-1234-1234", "https://example.com", "제육, 돈까스, 국밥");

        restaurantRepository.save(restaurant);
    }

    @Test
    public void testDelete () {

        Long id = 2L;

        restaurantRepository.deleteById(id);
    }

    @Test
    public void testPaging(){
        // 1 page order by bno desc
        Pageable pageable = PageRequest.of(0,10, Sort.by("id").descending());

        Page<Restaurant> result = restaurantRepository.findAll(pageable);

        log.info("total count : " + result.getTotalElements());
        log.info("total pages : " + result.getTotalPages());
        log.info("page number : " + result.getNumber());
        log.info("page size : " + result.getSize());

        List<Restaurant> todoList = result.getContent();

        todoList.forEach(restaurant -> log.info(restaurant));
    }


    @Test
    public void testInsertWithImages() {

        Restaurant restaurant = Restaurant.builder()
                .name("restaurant name")
                .address("address")
                .category("korean")
                .phoneNumber("010-1234-1234")
                .description("첨부파일 테스트")
                .build();

        for (int i = 0; i < 3; i++) {
            restaurant.addRestaurantImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }

        restaurantRepository.save(restaurant);

    }

    @Test
    public void testReadWithImages() {

        // 반드시 존재하는 id로 확인
        Optional<Restaurant> result = restaurantRepository.findByIdWithImages(101L);

        Restaurant restaurant = result.orElseThrow();

        log.info(restaurant);
        log.info("----------------");
        for(RestaurantImage restaurantImage : restaurant.getImageSet()) {
            log.info(restaurantImage);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages() {

        Optional<Restaurant> result = restaurantRepository.findByIdWithImages(101L);

        Restaurant restaurant = result.orElseThrow();

        restaurant.clearRestaurantImages();

        for(int i = 0; i < 2; i++){
            restaurant.addRestaurantImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
        }

        restaurantRepository.save(restaurant);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {
        Long id = 100L;
        restaurantRepository.deleteById(id);
    }

    @Test
    public void testInsertAll() {
        String[] categories = {"korean", "japanese", "italian", "chinese", "mexican"};
        String[] owners = {"kim", "lee", "park", "choi", "jeon", "json", "toby", "michel", "oh", "seok"};

        for(int i = 1; i <= 100; i++) {

            int k = i % categories.length;

            int m = i % owners.length;

            Restaurant restaurant = Restaurant.builder()
                    .name("restaurant name" + i)
                    .address("address" + i)
                    .category(categories[k])
                    .phoneNumber("010-1234-1234")
                    .description("delicious restaurant")
                    .ownerId(owners[m])
                    .build();

            for(int j = 0; j < 3; j++){

                if(i % 5 == 0) {
                    continue;
                }

                restaurant.addRestaurantImage(UUID.randomUUID().toString(), i + "file" + j + ".jpg");

            }
            restaurantRepository.save(restaurant);
        }

    }

    @Transactional
    @Test
    public void testSearchAll() {

        Pageable pageable = PageRequest.of(0,10,Sort.by("id").descending());

//        boardRepository.searchWithAll(null, null, pageable);

        Page<RestaurantListAllDTO> result = restaurantRepository.searchWithAll(null,null,pageable);

        log.info("-------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(restaurantListAllDTO ->  log.info(restaurantListAllDTO));

    }

    @Transactional
    @Test
    public void testFindByOwnerId() {
        String ownerId = "kim";

        List<Restaurant> restaurantList = restaurantRepository.findRestaurantByOwnerId(ownerId);

        log.info(restaurantList);
    }
}
