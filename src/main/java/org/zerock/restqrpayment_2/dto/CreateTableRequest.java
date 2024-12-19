package org.zerock.restqrpayment_2.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateTableRequest {
    @NotNull(message = "레스토랑 ID는 필수입니다.")
    private Long restaurantId;
}