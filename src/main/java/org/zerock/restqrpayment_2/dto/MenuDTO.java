package org.zerock.restqrpayment_2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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

    @Positive
    private Double price;

    @NotEmpty
    private String description;

    @NotEmpty
    private String dishes;

    // 첨부파일의 이름들
    private List<String> fileNames;
}
