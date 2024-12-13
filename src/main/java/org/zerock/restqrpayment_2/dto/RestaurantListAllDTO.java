package org.zerock.restqrpayment_2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantListAllDTO {
    private Long id;

    private String name;

    private String category;

    private LocalDateTime regDate;

    private List<RestaurantImageDTO> restaurantImages;
}
