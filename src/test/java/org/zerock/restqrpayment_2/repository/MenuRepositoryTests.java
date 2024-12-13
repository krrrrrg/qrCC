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
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.domain.MenuImage;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class MenuRepositoryTests {

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testInsert() {
        Long id = 100L;

        Restaurant restaurant = Restaurant.builder().id(id).build();

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name("menu name")
                .description("It is soooo delicious")
                .price(10000.0)
                .dishes("main dish")
                .build();

        menuRepository.save(menu);

    }


    @Test
    public void testInsertWithImages() {

        Long id = 46L;

        Restaurant restaurant = Restaurant.builder().id(id).build();

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name("new menu name")
                .description("It is soooo delicious")
                .price(10000.0)
                .dishes("main dish")
                .build();

        for (int i = 0; i < 3; i++) {
            menu.addMenuImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }

        menuRepository.save(menu);

    }

    @Test
    public void testReadWithImages() {

        // 반드시 존재하는 id로 확인
        Optional<Menu> result = menuRepository.findByIdWithImages(4L);

        Menu menu = result.orElseThrow();

        log.info(menu);
        log.info("----------------");
        for(MenuImage menuImage : menu.getImageSet()) {
            log.info(menuImage);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages() {

        Optional<Menu> result = menuRepository.findByIdWithImages(4L);

        Menu menu = result.orElseThrow();

        menu.clearMenuImages();

        for(int i = 0; i < 2; i++){
            menu.addMenuImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
        }

        menuRepository.save(menu);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveMenuWithImages() {
        Long id = 6L;

        menuRepository.deleteById(id);
    }


    @Test
    public void testInsertAll() {
        Double[] prices = {10000.0, 20000.0, 30000.0, 40000.0, 50000.0};
        String[] dishes = {"main dish", "side dish", "alcohol"};

        for(int i = 1; i < 1000; i++) {

            int k = i % dishes.length;

            int m = i % prices.length;

            Long id = (long)i % 100;

            if(id == 0L) {
                id = 99L;
            }

            Restaurant restaurant = Restaurant.builder().id(id).build();

            Menu menu = Menu.builder()
                    .restaurant(restaurant)
                    .name("menu name")
                    .description("It is soooo delicious")
                    .price(prices[m])
                    .dishes(dishes[k])
                    .build();

            for(int j = 0; j < 3; j++){

                if(i % 5 == 0) {
                    continue;
                }

                menu.addMenuImage(UUID.randomUUID().toString(), i + "file" + j + ".jpg");

            }
            menuRepository.save(menu);
        }

    }

    @Transactional
    @Test
    public void testSearchAll() {

        Long restaurantId = 24L;

        Pageable pageable = PageRequest.of(0,10,Sort.by("id").descending());

        Page<MenuListAllDTO> result = menuRepository.searchWithAll(restaurantId, pageable);

        log.info("-------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(menuListAllDTO -> log.info(menuListAllDTO));
    }
}
