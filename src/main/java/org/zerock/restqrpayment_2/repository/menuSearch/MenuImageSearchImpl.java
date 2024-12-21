package org.zerock.restqrpayment_2.repository.menuSearch;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.domain.MenuImage;
import org.zerock.restqrpayment_2.domain.QMenu;
import org.zerock.restqrpayment_2.domain.QMenuImage;
import org.zerock.restqrpayment_2.dto.MenuImageDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;

import java.util.*;
import java.util.stream.Collectors;

public class MenuImageSearchImpl extends QuerydslRepositorySupport implements MenuImageSearch {

    public MenuImageSearchImpl() {
        super(Menu.class);
    }

    @Override
    public Page<MenuListAllDTO> searchWithAll(Long restaurantId, Pageable pageable) {

        QMenu menu = QMenu.menu;
        QMenuImage menuImage = QMenuImage.menuImage;

        // JPQL 쿼리 정의
        JPQLQuery<Tuple> query = from(menu)
                .leftJoin(menu.imageSet, menuImage)
                .where(menu.restaurant.id.eq(restaurantId))
                .select(menu, menuImage);

        // 모든 데이터를 가져오기 (조인 결과 포함)
        List<Tuple> tupleList = query.fetch();

        // Menu와 관련된 이미지 데이터를 그룹화 및 정렬
        Map<Long, MenuListAllDTO> menuDtoMap = new LinkedHashMap<>();

        tupleList.forEach(tuple -> {
            Menu menuEntity = tuple.get(menu);

            // DTO 초기화 및 중복 방지
            menuDtoMap.computeIfAbsent(menuEntity.getId(), id -> MenuListAllDTO.builder()
                    .id(menuEntity.getId())
                    .name(menuEntity.getName())
                    .price(menuEntity.getPrice())
                    .description(menuEntity.getDescription())
                    .menuCategory(menuEntity.getMenuCategory())
                    .menuImages(new ArrayList<>()) // 빈 리스트 초기화
                    .build());

            // MenuImage 데이터 추가
            MenuImage menuImageEntity = tuple.get(menuImage);
            if (menuImageEntity != null) {
                menuDtoMap.get(menuEntity.getId()).getMenuImages()
                        .add(MenuImageDTO.builder()
                                .uuid(menuImageEntity.getUuid())
                                .fileName(menuImageEntity.getFileName())
                                .ord(menuImageEntity.getOrd())
                                .build());
            }
        });

        // DTO 리스트 생성 및 정렬
        List<MenuListAllDTO> sortedDtoList = menuDtoMap.values().stream()
                .peek(dto ->
                        // 이미지 리스트 정렬
                        dto.getMenuImages().sort(Comparator.comparingInt(MenuImageDTO::getOrd))
                )
                // category 기준 정렬
                .sorted(Comparator.comparing(MenuListAllDTO::getMenuCategory,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedDtoList.size());
        List<MenuListAllDTO> pagedDtoList = sortedDtoList.subList(start, end);

        // 총 데이터 개수 계산
        return new PageImpl<>(pagedDtoList, pageable, sortedDtoList.size());
    }

}
