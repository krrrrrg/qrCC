package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"imageSet", "menuSet"})  // 순환 참조 방지
@Table(name = "restaurant")
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column
    private String address;

    @Column
    private String phoneNumber;

    @Column
    private String description;

    @Column
    private String refLink;

    @Column(nullable = false)
    private String ownerId;

    @OneToMany(mappedBy = "restaurant",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<RestaurantImage> imageSet = new HashSet<>();

    @OneToMany(mappedBy = "restaurant",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<Menu> menuSet = new HashSet<>();

    public void addRestaurantImage(String uuid, String fileName) {
        RestaurantImage image = RestaurantImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .restaurant(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(image);
    }

    public void clearRestaurantImages() {
        imageSet.forEach(image -> image.changeRestaurant(null));
        imageSet.clear();
    }

    public void changeRestaurant(String name, String address, String category,
                               String phoneNumber, String description, String refLink) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.refLink = refLink;
    }

    public void addMenu(String name, Double price, String description) {
        Menu menu = Menu.builder()
                .name(name)
                .price(price)
                .description(description)
                .restaurant(this)
                .build();
        menuSet.add(menu);
    }

    public void clearMenuSet() {
        menuSet.forEach(menu -> menu.setRestaurant(null));
        menuSet.clear();
    }

    public void change(String name, String address, String phoneNumber, String description, String category) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.category = category;
    }

    public void changeRefLink(String refLink) {
        this.refLink = refLink;
    }
}
