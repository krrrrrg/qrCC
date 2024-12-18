package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Table(name = "restaurant_table")
public class RestaurantTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;
    
    private Integer tableNumber;
    
    private String qrCode;
    
    private String status;
} 