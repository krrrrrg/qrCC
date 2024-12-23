package org.zerock.restqrpayment_2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

// TODO: Add restaurant field
// TODO: Modify constructor

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private Long id;

    @NotEmpty(message = "메뉴 이름은 필수입니다")
    private String name;

    @NotNull(message = "가격은 필수입니다")
    private Double price;

    private String description;

    @NotEmpty(message = "카테고리는 필수입니다")
    private String menuCategory;

    private Long restaurantId;

    private List<String> fileNames;

    @Builder.Default
    private Set<MenuImageDTO> imageSet = new HashSet<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuImageDTO {
        private String uuid;
        private String fileName;
        private int ord;
    }
}
