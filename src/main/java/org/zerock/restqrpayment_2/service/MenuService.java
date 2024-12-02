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

    default Menu dtoToEntity(MenuDTO menuDTO) {
        Menu menu = Menu.builder()
                .id(menuDTO.getId())
                .name(menuDTO.getName())
                .price(menuDTO.getPrice())
                .description(menuDTO.getDescription())
                .dishes(menuDTO.getDishes())
                .build();

        if(menuDTO.getFileNames() != null) {
            menuDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                menu.addMenuImage(arr[0], arr[1]);
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
