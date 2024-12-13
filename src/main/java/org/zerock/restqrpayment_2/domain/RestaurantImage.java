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
@ToString(exclude = "restaurant")
public class RestaurantImage implements Comparable<RestaurantImage> {
    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @ManyToOne
    private Restaurant restaurant;

    // 이미지 순서
    @Override
    public int compareTo(RestaurantImage o) {
        return this.ord - o.ord;
    }

    // Restaurant 외부키 삭제
    public void changeRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
