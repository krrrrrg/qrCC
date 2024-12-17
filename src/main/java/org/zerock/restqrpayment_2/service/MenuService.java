package org.zerock.restqrpayment_2.service;

import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface MenuService {

    Long register(MenuDTO menuDTO);

    MenuDTO read(Long id);

    void modify(MenuDTO menuDTO);

    void remove(Long id);

    PageResponseDTO<MenuListAllDTO> listWithAll(Long restaurantId, PageRequestDTO pageRequestDTO);

    // 메뉴 카테고리 목록 조회
    List<String> getCategories(Long restaurantId);

    default Menu dtoToEntity(MenuDTO menuDTO) {
        Menu menu = Menu.builder()
                .id(menuDTO.getId())
                .name(menuDTO.getName())
                .price(menuDTO.getPrice())
                .description(menuDTO.getDescription())
                .dishes(menuDTO.getDishes())
                .build();

        if (menuDTO.getFileNames() != null) {
            menuDTO.getFileNames().forEach(fileName -> {
                String uuid = "";
                String originalFileName = fileName;
                
                if (fileName != null && fileName.contains("_")) {
                    int firstUnderscoreIdx = fileName.indexOf("_");
                    uuid = fileName.substring(0, firstUnderscoreIdx);
                    originalFileName = fileName.substring(firstUnderscoreIdx + 1);
                    menu.addMenuImage(uuid, originalFileName);
                } else {
                    throw new IllegalArgumentException("Invalid fileName or missing '_': " + fileName);
                }
            });
        }

        return menu;
    }


    default MenuDTO entityToDTO(Menu menu) {
        MenuDTO menuDTO = MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .dishes(menu.getDishes())
                .build();

        List<String> fileNames = menu.getImageSet().stream().sorted().map(menuImage ->
                menuImage.getUuid()+"_"+menuImage.getFileName()).collect(Collectors.toList());

        menuDTO.setFileNames(fileNames);

        return menuDTO;
    }
}
