package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"restaurant"})
@jakarta.persistence.Table(name = "restaurant_table")
public class RestaurantTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("tables")
    private Restaurant restaurant;
    
    @Column(nullable = false)
    private Integer tableNumber;
    
    private String qrCode;
    
    private String status;
}