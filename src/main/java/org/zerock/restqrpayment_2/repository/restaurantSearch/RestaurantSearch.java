package org.zerock.restqrpayment_2.repository.restaurantSearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;

public interface RestaurantSearch {
    Page<RestaurantListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);
}