package com.example.likelionback.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class ResponseDto {
    private Long id;
    private String userName;
    private String phoneNumber;
    private String email;
    private String profileUrl;
}