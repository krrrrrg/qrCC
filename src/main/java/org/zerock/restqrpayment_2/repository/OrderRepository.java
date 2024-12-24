package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.restqrpayment_2.domain.Order;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.domain.RestaurantTable;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTableId(Long tableId);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByTableIdAndStatus(Long tableId, Order.OrderStatus status);
    List<Order> findByRestaurantAndTableOrderByRegDateDesc(Restaurant restaurant, RestaurantTable table);
}
