package org.ktb.modie.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.ktb.modie.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final long expirationTime = 1000L * 60 * 60; // 1시간 (만료 시간 설정)

    @Value("${jwt.secret}")
    private String secretKey;

    // JWT 토큰 생성
    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUserId()); // 사용자 ID를 클레임으로 설정
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime); // 만료 시간 설정

        return Jwts.builder()
            .setSubject(user.getUserId())
            .setIssuedAt(now) // 발급 시간 설정
            .setExpiration(expiryDate) // 만료 시간 설정
            .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 비밀 키 설정
            .compact(); // 토큰 생성
    }

    // JWT 토큰에서 사용자 정보 추출
    public String extractUserId(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject(); // 사용자 ID 반환
    }

    // JWT 토큰 유효성 검사
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return expiration.before(new Date());
    }

    // JwtService.java
    public boolean isTokenValid(String token) {
        try {
            // 토큰의 서명을 검증합니다.
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            // 서명이 올바르다면 만료 여부를 확인하여 유효성을 반환합니다.
            return !isTokenExpired(token);
        } catch (Exception e) {
            // 파싱 과정에서 예외가 발생하면 토큰이 유효하지 않다고 판단합니다.
            return false;
        }
    }

}
