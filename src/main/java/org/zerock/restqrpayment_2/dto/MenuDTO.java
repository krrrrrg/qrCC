package org.zerock.restqrpayment_2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO: Add restaurant field
// TODO: Modify constructor

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private Long id;

    @NotNull
    private Long restaurantId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String category;  // 메뉴 카테고리

    @Positive
    private Double price;

    @NotEmpty
    private String description;

    @NotEmpty
    private String dishes;

    // 첨부파일의 이름들
    private List<String> fileNames;
}
