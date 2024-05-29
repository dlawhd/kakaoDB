package com.example.likelionback.service.impl;

import com.example.likelionback.data.dto.ResponseDto;
import com.example.likelionback.data.entity.LikeLion;
import com.example.likelionback.data.repositoty.LikeLionRepository;
import com.example.likelionback.service.AuthService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${kakao.client.id}")
    private String clientKey;
    @Value("${kakao.redirect.url}")
    private String redirectUrl;
    @Value("${kakao.accesstoken.url}")
    private String kakaoAccessTokenUrl;
    @Value("${kakao.userinfo.url}")
    private String kakaoUserInfoUrl;

    private final LikeLionRepository likeLionRepository;
    @Override
    public ResponseEntity<?> getKaKaoUserInfo(String authorizeCode) {
        log.info("[kakao login] issue a authorizecode");
        ObjectMapper objectMapper = new ObjectMapper(); // json 파싱해주는 객체
        RestTemplate restTemplate = new RestTemplate(); // client 연결 객체

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientKey);
        params.add("redirect_uri", redirectUrl);
        params.add("code", authorizeCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    kakaoAccessTokenUrl,
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );
            log.info("[kakao login] authorizecode issued successfully");
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            String accessToken = (String) responseMap.get("access_token");

            ResponseDto userInfoDto = getInfo(accessToken);

            // 사용자 정보를 데이터베이스에 저장
            LikeLion likeLion = new LikeLion();
            likeLion.setUserName(userInfoDto.getUserName());
            likeLion.setPhoneNumber(userInfoDto.getPhoneNumber());
            likeLion.setEmail(userInfoDto.getEmail());
            likeLion.setProfileUrl(userInfoDto.getProfileUrl());

            // 데이터베이스에 저장
            likeLionRepository.save(likeLion);

            return ResponseEntity.ok(userInfoDto);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    private ResponseDto getInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoUserInfoUrl, entity, String.class);

        try {
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseMap.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            ResponseDto requestSignUpDto = ResponseDto.builder()
                    .userName((String) kakaoAccount.get("name"))
                    .phoneNumber((String) kakaoAccount.get("phone_number"))
                    .email((String) kakaoAccount.get("email"))
                    .profileUrl((String) profile.get("profile_image_url"))
                    .build();

            return requestSignUpDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}