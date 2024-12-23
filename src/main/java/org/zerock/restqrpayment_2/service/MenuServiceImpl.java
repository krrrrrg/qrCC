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

import java.util.ArrayList;
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
            menu.setFileNames(new ArrayList<>(menuDTO.getFileNames()));
            
            // 이미지 처리
            for (String fileName : menuDTO.getFileNames()) {
                String uuid = extractUUID(fileName);
                String originalFileName = extractFileName(fileName);
                menu.addMenuImage(uuid, originalFileName);
            }
        }

        Menu savedMenu = menuRepository.save(menu);
        return modelMapper.map(savedMenu, MenuDTO.class);
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
            // 기존 이미지 초기화
            menu.getImageSet().clear();
            menu.setFileNames(new ArrayList<>(menuDTO.getFileNames()));
            
            // 새 이미지 추가
            for (String fileName : menuDTO.getFileNames()) {
                String uuid = extractUUID(fileName);
                String originalFileName = extractFileName(fileName);
                menu.addMenuImage(uuid, originalFileName);
            }
        }

        Menu updatedMenu = menuRepository.save(menu);
        return modelMapper.map(updatedMenu, MenuDTO.class);
    }

    private String extractUUID(String fileName) {
        int underscoreIndex = fileName.indexOf("_");
        if (underscoreIndex > 0) {
            return fileName.substring(0, underscoreIndex);
        }
        return UUID.randomUUID().toString();
    }

    private String extractFileName(String fileName) {
        int underscoreIndex = fileName.indexOf("_");
        if (underscoreIndex > 0 && underscoreIndex < fileName.length() - 1) {
            return fileName.substring(underscoreIndex + 1);
        }
        return fileName;
    }

    @Override
    public List<MenuDTO> getMenusByRestaurant(Long restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        return menus.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMenusByRestaurantAndOwner(Long restaurantId, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        return menus.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
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
