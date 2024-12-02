package org.zerock.restqrpayment_2.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = "menu")
public class MenuImage implements Comparable<MenuImage> {
    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @ManyToOne
    private Menu menu;

    @Override
    public int compareTo(MenuImage o) {
        return this.ord - o.ord;
    }

    // 메뉴 외부키 삭제
    public void changeMenu(Menu menu) {
        this.menu = menu;
    }
}

