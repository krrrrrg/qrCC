package org.zerock.restqrpayment_2.repository.menuSearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;

public interface MenuImageSearch {
    Page<MenuListAllDTO> searchWithAll(Long restaurantId, Pageable pageable);

}
