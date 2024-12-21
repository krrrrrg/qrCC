package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.domain.MenuImage;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.repository.MenuRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

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

        if (menuDTO.getFileNames() != null && !menuDTO.getFileNames().isEmpty()) {
            menu.setFileNames(menuDTO.getFileNames());
        }

        Menu savedMenu = menuRepository.save(menu);

        return MenuDTO.builder()
                .id(savedMenu.getId())
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .description(savedMenu.getDescription())
                .menuCategory(savedMenu.getMenuCategory())
                .restaurantId(savedMenu.getRestaurant().getId())
                .fileNames(savedMenu.getFileNames())
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
        menu.setMenuCategory(menuDTO.getMenuCategory());
        
        if (menuDTO.getFileNames() != null && !menuDTO.getFileNames().isEmpty()) {
            menu.setFileNames(menuDTO.getFileNames());
        }

        Menu updatedMenu = menuRepository.save(menu);

        return MenuDTO.builder()
                .id(updatedMenu.getId())
                .name(updatedMenu.getName())
                .price(updatedMenu.getPrice())
                .description(updatedMenu.getDescription())
                .menuCategory(updatedMenu.getMenuCategory())
                .restaurantId(updatedMenu.getRestaurant().getId())
                .fileNames(updatedMenu.getFileNames())
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
                .fileNames(menu.getFileNames())
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
                .fileNames(menu.getFileNames())
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

    @Override
    @Transactional
    public void addImages(Long menuId, List<String> fileNames) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        // Add new file names to the existing list
        List<String> existingFiles = menu.getFileNames();
        existingFiles.addAll(fileNames);
        
        // Create MenuImage entities
        int startOrd = menu.getImageSet().size();
        fileNames.forEach(fileName -> {
            MenuImage image = MenuImage.builder()
                    .uuid(UUID.randomUUID().toString())
                    .fileName(fileName)
                    .ord(startOrd + fileNames.indexOf(fileName))
                    .menu(menu)
                    .build();
            menu.getImageSet().add(image);
        });

        menuRepository.save(menu);
    }

    @Override
    public MenuDTO getMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + menuId));
        
        return modelMapper.map(menu, MenuDTO.class);
    }
}
