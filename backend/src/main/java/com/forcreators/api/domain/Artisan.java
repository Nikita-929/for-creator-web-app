package com.forcreators.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artisans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artisan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 4000)
    private String bio;

    private String photo;

    private String workshopLocation;

    @Column(length = 4000)
    private String processDescription;

    @OneToMany(mappedBy = "artisan")
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
