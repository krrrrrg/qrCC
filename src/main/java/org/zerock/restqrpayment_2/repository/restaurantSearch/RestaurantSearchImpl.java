package org.zerock.restqrpayment_2.repository.restaurantSearch;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.restqrpayment_2.domain.QRestaurant;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;

import java.util.List;

public class RestaurantSearchImpl extends QuerydslRepositorySupport implements RestaurantSearch {

    public RestaurantSearchImpl() {
        super(Restaurant.class);
    }

    @Override
    public Page<RestaurantListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        JPQLQuery<Restaurant> query = from(restaurant);

        if ((types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(restaurant.name.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(restaurant.category.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(restaurant.description.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        JPQLQuery<RestaurantListAllDTO> dtoQuery = query.select(Projections.bean(
                RestaurantListAllDTO.class,
                restaurant.id,
                restaurant.name,
                restaurant.category,
                restaurant.address,
                restaurant.phoneNumber,
                restaurant.description
        ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);
        List<RestaurantListAllDTO> dtoList = dtoQuery.fetch();
        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }
}