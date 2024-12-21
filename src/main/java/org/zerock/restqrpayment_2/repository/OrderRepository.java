package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.restqrpayment_2.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
