package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.repository.MenuRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public MenuDTO createMenu(MenuDTO menuDTO, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(menuDTO.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        Menu menu = Menu.builder()
                .name(menuDTO.getName())
                .price(menuDTO.getPrice())
                .description(menuDTO.getDescription())
                .menuCategory(menuDTO.getMenuCategory())
                .restaurant(restaurant)
                .build();

        Menu savedMenu = menuRepository.save(menu);

        return MenuDTO.builder()
                .id(savedMenu.getId())
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .description(savedMenu.getDescription())
                .menuCategory(savedMenu.getMenuCategory())
                .restaurantId(savedMenu.getRestaurant().getId())
                .build();
    }

    @Override
    public MenuDTO updateMenu(Long menuId, MenuDTO menuDTO, String ownerId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        if (!menu.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        menu.changeMenu(menuDTO.getName(), menuDTO.getPrice(), menuDTO.getDescription());
        Menu updatedMenu = menuRepository.save(menu);

        return MenuDTO.builder()
                .id(updatedMenu.getId())
                .name(updatedMenu.getName())
                .price(updatedMenu.getPrice())
                .description(updatedMenu.getDescription())
                .menuCategory(updatedMenu.getMenuCategory())
                .restaurantId(updatedMenu.getRestaurant().getId())
                .build();
    }

    @Override
    public List<MenuDTO> getMenusByRestaurant(Long restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        
        return menus.stream()
            .sorted(Comparator.comparing(Menu::getMenuCategory, 
                Comparator.nullsLast(Comparator.naturalOrder())))
            .map(menu -> MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuCategory(menu.getMenuCategory())
                .description(menu.getDescription())
                .restaurantId(menu.getRestaurant().getId())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMenusByRestaurantAndOwner(Long restaurantId, String ownerId) {
        List<Menu> menus = menuRepository.findByRestaurantIdAndOwnerId(restaurantId, ownerId);
        
        return menus.stream()
            .sorted(Comparator.comparing(Menu::getMenuCategory, 
                Comparator.nullsLast(Comparator.naturalOrder())))
            .map(menu -> MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuCategory(menu.getMenuCategory())
                .description(menu.getDescription())
                .restaurantId(menu.getRestaurant().getId())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void deleteMenu(Long menuId, String ownerId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        if (!menu.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        menuRepository.delete(menu);
    }
}
