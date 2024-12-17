package org.zerock.restqrpayment_2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.zerock.restqrpayment_2.domain.Restaurant;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 100)
    private String name;

    @NotEmpty
    private String address;

    @NotEmpty
    private String phoneNumber;

    private String refLink;

    private String description;

    @NotEmpty
    private String ownerId;

    private String openTime;
    
    private String closeTime;

    private List<String> fileNames;

    @Builder.Default
    private String category = "기타";

    public Restaurant toEntity() {
        return Restaurant.builder()
                .id(id)
                .name(name)
                .category(category)
                .businessType(category)
                .address(address)
                .phoneNumber(phoneNumber)
                .description(description)
                .refLink(refLink)
                .ownerId(ownerId)
                .build();
    }
}
