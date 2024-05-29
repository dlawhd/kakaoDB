package com.example.likelionback.data.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "kakaoinformation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LikeLion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String profileUrl;
}