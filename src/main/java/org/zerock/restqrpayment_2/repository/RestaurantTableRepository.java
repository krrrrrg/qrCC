package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.restqrpayment_2.domain.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
}
