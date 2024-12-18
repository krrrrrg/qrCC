package org.zerock.restqrpayment_2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantListAllDTO {
    private Long id;
    private String name;
    private String category;
    private String address;
    private String phoneNumber;
    private String description;
}
