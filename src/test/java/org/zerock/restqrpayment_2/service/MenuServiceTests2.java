package org.zerock.restqrpayment_2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.repository.MenuRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MenuServiceTests2 {

    @InjectMocks
    private MenuServiceImpl menuService;

    @Mock
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        // Given
        MenuDTO menuDTO = MenuDTO.builder()
                .name("Test Menu")
                .price(1000.0)
                .description("Test Description")
                .dishes("Test Dishes")
                .fileNames(Arrays.asList(UUID.randomUUID() + "_image1.jpg", UUID.randomUUID() + "_image2.jpg"))
                .build();

        Menu menu = Menu.builder()
                .id(1L)
                .name(menuDTO.getName())
                .price(menuDTO.getPrice())
                .description(menuDTO.getDescription())
                .build();

        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // When
        Long id = menuService.register(menuDTO);

        // Then
        assertThat(id).isEqualTo(1L);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void testRead() {
        // Given
        Long menuId = 1L;
        UUID uuid = UUID.randomUUID();
        Menu menu = Menu.builder()
                .id(menuId)
                .name("Test Menu")
                .price(1000.0)
                .description("Test Description")
                .build();
        menu.addMenuImage(uuid.toString(), "image1.jpg");

        when(menuRepository.findByIdWithImages(menuId)).thenReturn(Optional.of(menu));

        // When
        MenuDTO menuDTO = menuService.read(menuId);

        // Then
        assertThat(menuDTO.getId()).isEqualTo(menuId);
        assertThat(menuDTO.getName()).isEqualTo("Test Menu");
        assertThat(menuDTO.getFileNames()).containsExactly(uuid + "_image1.jpg");
        verify(menuRepository, times(1)).findByIdWithImages(menuId);
    }

    @Test
    void testModify() {
        // Given
        Long menuId = 1L;
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Menu menu = Menu.builder()
                .id(menuId)
                .name("Old Menu")
                .price(1000.0)
                .description("Old Description")
                .build();

        MenuDTO menuDTO = MenuDTO.builder()
                .id(menuId)
                .name("Updated Menu")
                .price(1500.0)
                .description("Updated Description")
                .fileNames(Arrays.asList(uuid1 + "_image1.jpg", uuid2 + "_image2.jpg"))
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // When
        menuService.modify(menuDTO);

        // Then
        assertThat(menu.getName()).isEqualTo("Updated Menu");
        assertThat(menu.getPrice()).isEqualTo(1500);
        assertThat(menu.getDescription()).isEqualTo("Updated Description");
        assertThat(menu.getImageSet().size()).isEqualTo(2);
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void testRemove() {
        // Given
        Long menuId = 1L;

        // When
        menuService.remove(menuId);

        // Then
        verify(menuRepository, times(1)).deleteById(menuId);
    }

    @Test
    void testListWithAll() {
        // Given
        Long restaurantId = 1L;
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .link(null)
                .build();

        // When
        PageResponseDTO<MenuListAllDTO> response = menuService.listWithAll(restaurantId, pageRequestDTO);
    }
}

