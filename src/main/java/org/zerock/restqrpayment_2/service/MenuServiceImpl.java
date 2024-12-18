package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.MenuImageDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.repository.MenuRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public Long register(MenuDTO menuDTO) {
        Restaurant restaurant = restaurantRepository.findById(menuDTO.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + menuDTO.getRestaurantId()));

        Menu menu = dtoToEntity(menuDTO);
        menu.setRestaurant(restaurant);

        log.info(menu);
        return menuRepository.save(menu).getId();
    }

    @Override
    public MenuDTO read(Long id) {
        Optional<Menu> menuOptional = menuRepository.findByIdWithImages(id);
        Menu menu = menuOptional.orElseThrow();
        return entityToDTO(menu);
    }

    @Override
    public void modify(MenuDTO menuDTO) {
        Optional<Menu> menuOptional = menuRepository.findById(menuDTO.getId());
        Menu menu = menuOptional.orElseThrow();

        menu.changeMenu(menuDTO.getName(), menuDTO.getPrice(), menuDTO.getDescription());
        menu.clearMenuImages();

        if(menuDTO.getFileNames() != null) {
            for(String fileName : menuDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                menu.addMenuImage(arr[0], arr[1]);
            }
        }

        menuRepository.save(menu);
    }

    @Override
    public void remove(Long id) {
        menuRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<MenuListAllDTO> listWithAll(Long restaurantId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("id");
        
        Page<Menu> result = menuRepository.findByRestaurantId(restaurantId, pageable);
        
        List<MenuListAllDTO> dtoList = result.getContent().stream()
            .map(menu -> MenuListAllDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .restaurantId(restaurantId)
                // 필요한 다른 필드들 추가
                .build())
            .collect(Collectors.toList());
        
        return PageResponseDTO.<MenuListAllDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total(result.getTotalElements())
            .build();
    }

    @Override
    public List<String> getCategories(Long restaurantId) {
        return menuRepository.findCategoriesByRestaurantId(restaurantId);
    }

    @Override
    public List<MenuDTO> getMenusByRestaurantAndOwner(Long restaurantId, String ownerId) {
        // 먼저 레스토랑이 해당 소유자의 것인지 확인
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(restaurantId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found or not owned by user"));

        // 해당 레스토랑의 메뉴 목록 조회
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        return menus.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MenuDTO createMenu(MenuDTO menuDTO, String ownerId) {
        // 레스토랑 소유자 확인
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(menuDTO.getRestaurantId(), ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found or not owned by user"));

        Menu menu = dtoToEntity(menuDTO);
        menu.setRestaurant(restaurant);
        
        Menu savedMenu = menuRepository.save(menu);
        return entityToDTO(savedMenu);
    }

    @Override
    public MenuDTO updateMenu(Long menuId, MenuDTO menuDTO, String ownerId) {
        // 메뉴가 해당 소유자의 레스토랑에 속하는지 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        if (!menu.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized to update this menu");
        }

        menu.changeMenu(menuDTO.getName(), menuDTO.getPrice(), menuDTO.getDescription());
        menu.clearMenuImages();

        if (menuDTO.getFileNames() != null) {
            menuDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                menu.addMenuImage(arr[0], arr[1]);
            });
        }

        Menu updatedMenu = menuRepository.save(menu);
        return entityToDTO(updatedMenu);
    }

    @Override
    public void deleteMenu(Long menuId, String ownerId) {
        // 메뉴가 해당 소유자의 레스토랑에 속하는지 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        if (!menu.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized to delete this menu");
        }

        menuRepository.delete(menu);
    }
}
