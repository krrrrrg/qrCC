package org.zerock.restqrpayment_2.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {

    private String uuid;

    private String fileName;

    private boolean img;

    public String getLink() {
        if(img) { // 이미지인 경우 썸네일로 반환
            return "s_" + uuid + "_" + fileName;
        } else {
            return uuid + "_" + fileName;
        }
    }

}