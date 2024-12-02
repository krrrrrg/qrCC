package org.zerock.restqrpayment_2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.repository.menuSearch.MenuImageSearch;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuImageSearch {
    // 메뉴 id 받아서 이미지랑 메뉴 정보 찾기
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select m from Menu m where m.id =:menuId")
    Optional<Menu> findByIdWithImages(Long menuId);
}
