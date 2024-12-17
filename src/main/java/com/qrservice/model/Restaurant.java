package com.qrservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "restaurants")
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String category;
    
    @Column
    private String address;
    
    @Column
    private String phoneNumber;
    
    @Column
    private String openTime;
    
    @Column
    private String closeTime;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private String refLink;
    
    @Column(nullable = false)
    private String ownerId;
}
