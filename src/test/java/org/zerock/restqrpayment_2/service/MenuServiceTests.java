package org.zerock.restqrpayment_2.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;

@SpringBootTest
@Log4j2
public class MenuServiceTests {

    @Autowired
    private MenuService menuService;

    @Test
    public void testRegister() {

        MenuDTO menuDTO = MenuDTO.builder()
                .name("menu name")
                .description("menu description")
                .price(20000.0)
                .dishes("main menu")
                .restaurantId(100L)
                .build();

        log.info(menuService.register(menuDTO));

    }

    @Test
    public void testGetById() {
        Long id = 2L;

        MenuDTO menuDTO = menuService.read(id);

        log.info(menuDTO);
    }

    @Test
    public void testUpdate() {
        Long id = 1L;

        MenuDTO menuDTO = MenuDTO.builder()
                .id(id)
                .name("updated menu name")
                .description("updated menu description")
                .price(20000.0)
                .dishes("updated main menu")
                .restaurantId(100L)
                .build();

        menuService.modify(menuDTO);

    }

    @Test
    public void testGetListWithImages() {

        Long restaurantId = 12L;
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .link(null)
                .build();

        PageResponseDTO<MenuListAllDTO> listOfBoard = menuService.listWithAll(restaurantId, pageRequestDTO);

        log.info(listOfBoard);
    }
}
