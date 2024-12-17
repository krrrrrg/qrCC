package org.zerock.restqrpayment_2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select m, mi from Menu m left outer join MenuImage mi on mi.menu = m where m.id = :id")
    Optional<Menu> findByIdWithImages(@Param("id") Long id);

    @Query("select m, mi from Menu m left outer join MenuImage mi on mi.menu = m where m.restaurant.id = :restaurantId")
    Page<Object[]> searchWithAll(@Param("restaurantId") Long restaurantId, Pageable pageable);

    @Query("SELECT DISTINCT m.dishes FROM Menu m WHERE m.restaurant.id = :restaurantId AND m.dishes IS NOT NULL ORDER BY m.dishes")
    List<String> findCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 레스토랑 ID로 메뉴 목록 조회
    @Query("SELECT m FROM Menu m WHERE m.restaurant.id = :restaurantId")
    List<Menu> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 레스토랑 ID와 소유자 ID로 메뉴 목록 조회
    @Query("SELECT m FROM Menu m WHERE m.restaurant.id = :restaurantId AND m.restaurant.ownerId = :ownerId")
    List<Menu> findByRestaurantIdAndOwnerId(@Param("restaurantId") Long restaurantId, @Param("ownerId") String ownerId);
}
