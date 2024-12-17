package org.zerock.restqrpayment_2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String businessType;

    @NotEmpty
    private String phoneNumber;

    private String refLink;

    private String description;

    @NotEmpty
    private String ownerId;

    private String openTime;
    
    private String closeTime;

    // 첨부파일의 이름들
    private List<String> fileNames;

}
