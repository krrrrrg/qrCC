package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// restaurant 제외
@ToString(exclude = "restaurant")
@Entity
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name; // 메뉴 이름

    private Double price; // 메뉴 가격

    private String description; // 메뉴 설명

    private String dishes;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant; // Restaurant과 연관 관계

    // 메뉴 업데이트
    public void changeMenu(String name, Double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // 외부키 삭제
    public void clearRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @OneToMany(mappedBy = "menu",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<MenuImage> imageSet = new HashSet<>();

    // 메뉴 이미지 추가
    public void addMenuImage(String uuid, String fileName) {
        MenuImage menuImage = MenuImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .menu(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(menuImage);
    }

    // 메뉴 이미지 삭제
    public void clearMenuImages() {
        imageSet.forEach(menuImage -> menuImage.changeMenu(null));

        this.imageSet.clear();
    }
}
