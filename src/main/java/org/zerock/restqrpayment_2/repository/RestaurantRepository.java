package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.repository.restaurantSearch.RestaurantSearch;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantSearch {

    // id를 받아 레스토랑 이미지 찾기
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select r from Restaurant r where r.id =:id")
    Optional<Restaurant> findByIdWithImages(Long id);

    // ownerId 받아 restaurants 찾기
    @Query("select r from Restaurant r where r.ownerId =:ownerId")
    List<Restaurant> findRestaurantByOwnerId(String ownerId);
}
