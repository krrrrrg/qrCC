package org.zerock.restqrpayment_2.service;

import org.zerock.restqrpayment_2.dto.MenuDTO;
import java.util.List;

public interface MenuService {
    MenuDTO createMenu(MenuDTO menuDTO, String ownerId);
    MenuDTO updateMenu(Long menuId, MenuDTO menuDTO, String ownerId);
    void deleteMenu(Long menuId, String ownerId);
    List<MenuDTO> getMenusByRestaurant(Long restaurantId);
    List<MenuDTO> getMenusByRestaurantAndOwner(Long restaurantId, String ownerId);
    void addImages(Long menuId, List<String> fileNames);
}
