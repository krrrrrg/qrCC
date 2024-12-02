package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zerock.restqrpayment_2.domain.Member;
import org.zerock.restqrpayment_2.domain.Menu;
import org.zerock.restqrpayment_2.domain.Restaurant;
import org.zerock.restqrpayment_2.dto.MenuDTO;
import org.zerock.restqrpayment_2.dto.MenuListAllDTO;
import org.zerock.restqrpayment_2.dto.PageRequestDTO;
import org.zerock.restqrpayment_2.dto.PageResponseDTO;
import org.zerock.restqrpayment_2.repository.MenuRepository;
import org.zerock.restqrpayment_2.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class MenuServiceImpl implements MenuService {

    private final RestaurantRepository restaurantRepository;

    private final MenuRepository menuRepository;

    private final ModelMapper modelMapper;

    @Override
    public Long register(MenuDTO menuDTO) {

        Menu menu = modelMapper.map(menuDTO, Menu.class);

        log.info(menu);

        Long id = menuRepository.save(menu).getId();

        return id;
    }

    @Override
    public MenuDTO read(Long id) {

        Optional<Menu> menuOptional = menuRepository.findById(id);

        Menu menu = menuOptional.orElseThrow();

        return modelMapper.map(menu, MenuDTO.class);
    }

    @Override
    public void modify(MenuDTO menuDTO) {

        Optional<Menu> menuOptional = menuRepository.findById(menuDTO.getId());

        Menu menu = menuOptional.orElseThrow();

        menu.changeMenu(menuDTO.getName(), menuDTO.getPrice(), menu.getDescription());

        menuRepository.save(menu);

    }

    @Override
    public void remove(Long id) {
        menuRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<MenuListAllDTO> listWithAll(Long restaurantId, PageRequestDTO pageRequestDTO) {

        // 페이지 구성 - 0보다 작으면 0 페이지, 1보다 크면 -1  페이지
        // 사이즈 default 값
        // 정렬 rno 역정렬
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("id").descending());

        Page<MenuListAllDTO> result = menuRepository.searchWithAll(restaurantId, pageable);

        return PageResponseDTO.<MenuListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

}
