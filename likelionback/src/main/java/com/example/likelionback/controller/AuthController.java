package com.example.likelionback.controller;

import com.example.likelionback.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping("/kakao/callback")
        public ResponseEntity<?> getKaKaoAuthorizeCode(@RequestParam("code") String authorizeCode){
            log.info("[kakao-login] authorizeCode : {}", authorizeCode);
            return authService.getKaKaoUserInfo(authorizeCode);
        }
    }

