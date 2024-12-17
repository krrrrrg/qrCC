package org.zerock.restqrpayment_2.repository.restaurantSearch;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.restqrpayment_2.domain.QRestaurant;
import org.zerock.restqrpayment_2.domain.QRestaurantImage;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.RestaurantImageDTO;
import org.zerock.restqrpayment_2.dto.RestaurantListAllDTO;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class RestaurantSearchImpl extends QuerydslRepositorySupport implements RestaurantSearch {

    public RestaurantSearchImpl() {
        super(Restaurant.class);
    }

    @Override
    public Page<RestaurantListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        QRestaurantImage restaurantImage = QRestaurantImage.restaurantImage;

        // Restaurant 기본 조회
        JPQLQuery<Restaurant> baseQuery = from(restaurant);

        // 검색 조건 추가
        if (types != null && types.length > 0 && keyword != null) {
            baseQuery.where(createSearchPredicate(types, keyword, restaurant));
        }

        // 페이징 처리
        getQuerydsl().applyPagination(pageable, baseQuery);

        // Restaurant 조회 결과
        List<Restaurant> restaurantList = baseQuery.fetch();
        if (restaurantList.isEmpty()) {
            // 데이터가 없을 경우 빈 페이지 반환
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 조회된 Restaurant IDs
        List<Long> restaurantIds = restaurantList.stream()
                .map(Restaurant::getId)
                .collect(Collectors.toList());

        // 이미지 조회 및 그룹화
        Map<Long, List<RestaurantImageDTO>> imageMap = from(restaurantImage)
                .where(restaurantImage.restaurant.id.in(restaurantIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        image -> image.getRestaurant().getId(), // 그룹화 키로 restaurantId 사용
                        LinkedHashMap::new, // 순서를 보존
                        Collectors.mapping(
                                image -> RestaurantImageDTO.builder()
                                        .uuid(image.getUuid())
                                        .fileName(image.getFileName())
                                        .ord(image.getOrd())
                                        .build(),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator.comparingInt(RestaurantImageDTO::getOrd)) // ord 기준 정렬
                                                .collect(Collectors.toList())
                                )
                        )
                ));

        // Restaurant 데이터를 DTO로 변환
        List<RestaurantListAllDTO> dtoList = restaurantList.stream()
                .map(restaurant1 -> RestaurantListAllDTO.builder()
                        .id(restaurant1.getId())
                        .name(restaurant1.getName())
                        .category(restaurant1.getCategory())
                        .regDate(restaurant1.getRegDate())
                        .restaurantImages(imageMap.getOrDefault(restaurant1.getId(), Collections.emptyList()))
                        .build())
                .collect(Collectors.toList());

        // 총 개수 계산
        long totalCount = baseQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    /**
     * 검색 조건을 생성하는 헬퍼 메서드
     */
    private BooleanBuilder createSearchPredicate(String[] types, String keyword, QRestaurant restaurant) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String type : types) {
            switch (type) {
                case "n":
                    booleanBuilder.or(restaurant.name.contains(keyword));
                    break;
                case "c":
                    booleanBuilder.or(restaurant.category.contains(keyword));
                    break;
                case "a":
                    booleanBuilder.or(restaurant.address.contains(keyword));
                    break;
                case "o":
                    booleanBuilder.or(restaurant.ownerId.eq(keyword));
                    break;
            }
        }
        return booleanBuilder;
    }
}