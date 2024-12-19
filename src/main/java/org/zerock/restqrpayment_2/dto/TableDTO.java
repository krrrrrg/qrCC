package org.zerock.restqrpayment_2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    private Long id;
    private Integer tableNumber;
    private String status;
    private Long restaurantId;
    private String restaurantName;
}
