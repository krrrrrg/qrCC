package org.zerock.restqrpayment_2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuListAllDTO {

    private Long id;

    private String name;

    private Double price;

    private String description;

    private String menuCategory;

    private Long restaurantId;

    private List<MenuImageDTO> menuImages;

    public String getCategory() {
        return menuCategory;
    }

    public void setCategory(String category) {
        this.menuCategory = category;
    }

}
