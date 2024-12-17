package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.repository.restaurantSearch.RestaurantSearch;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantSearch {

    // id를 받아 레스토랑 이미지 찾기
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select r from Restaurant r where r.id =:id")
    Optional<Restaurant> findByIdWithImages(@Param("id") Long id);

    // ownerId 받아 restaurants 찾기
    @Query("select r from Restaurant r where r.ownerId =:ownerId")
    List<Restaurant> findRestaurantByOwnerId(@Param("ownerId") String ownerId);

    // 레스토랑 ID와 소유자 ID로 레스토랑 찾기
    @Query("SELECT r FROM Restaurant r WHERE r.id = :id AND r.ownerId = :ownerId")
    Optional<Restaurant> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") String ownerId);
}
