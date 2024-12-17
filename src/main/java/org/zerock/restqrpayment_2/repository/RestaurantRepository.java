package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.repository.restaurantSearch.RestaurantSearch;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantSearch {
    List<Restaurant> findByOwnerId(String ownerId);
    
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select r from Restaurant r where r.id = :id")
    Optional<Restaurant> findByIdWithImages(@Param("id") Long id);

    @Query("select r from Restaurant r where r.id = :id and r.ownerId = :ownerId")
    Optional<Restaurant> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") String ownerId);
}
