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
@ToString
@Entity
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(length = 500, nullable = false)
    private String address;

    @Column(length = 500, nullable = false)
    private String category;

    @Column(length = 500, nullable = false)
    private String phoneNumber;

    private String refLink;

    private String description;

    // 레스토랑 주인 구별 프로퍼티
    private String ownerId;

    // Restaurant 업데이트
    public void changeRestaurant(String name, String address, String category, String phoneNumber, String refLink, String description) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.refLink = refLink;
        this.description = description;
    }

    @OneToMany(mappedBy = "restaurant",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<RestaurantImage> imageSet = new HashSet<>();

    // RestaurantImage 추가
    public void addRestaurantImage(String uuid, String fileName) {
        RestaurantImage restaurantImage = RestaurantImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .restaurant(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(restaurantImage);
    }

    // RestaurantImage 삭제
    public void clearRestaurantImages() {
        imageSet.forEach(restaurantImage -> restaurantImage.changeRestaurant(null));

        this.imageSet.clear();
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Menu> menuSet = new HashSet<>();

    // 메뉴 추가
    public void addMenu(String name, Double price, String description) {
        Menu menu = Menu.builder()
                .name(name)
                .price(price)
                .description(description)
                .restaurant(this)
                .build();
        menuSet.add(menu);
    }

    // 메뉴 삭제
    public void clearMenus() {
        menuSet.forEach(menu -> menu.clearRestaurant(null));
        this.menuSet.clear();
    }
}
