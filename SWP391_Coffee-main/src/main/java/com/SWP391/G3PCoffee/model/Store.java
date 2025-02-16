/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.model;

/**
 *
 * @author Anh
 */
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private Long manager_id;
    @Column(name = "open_time")
    private LocalDateTime openTime;

    @Column(name = "closing_time")
    private LocalDateTime closingTime;
    @PrePersist
    protected void onCreate() {
        this.openTime = LocalDateTime.now();
        this.closingTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.openTime = LocalDateTime.now();
        this.closingTime = LocalDateTime.now();
    }
}

