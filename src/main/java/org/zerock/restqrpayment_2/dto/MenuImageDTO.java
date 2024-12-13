package org.zerock.restqrpayment_2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuImageDTO {
    private String uuid;

    private String fileName;

    private int ord;
}
