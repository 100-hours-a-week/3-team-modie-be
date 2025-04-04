package org.ktb.modie.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.util.HashIdUtil;
import org.ktb.modie.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

	private final long expirationTime = 1000L * 60 * 60 * 24 * 30; // 1개월
	private final HashIdUtil hashIdUtil;

	@Value("${jwt.secret}")
	private String secretKey;

	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// JWT 토큰 생성
	public String createToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationTime); // 만료 시간 설정

		// userId Hash encode
		String userId = hashIdUtil.encode(Long.parseLong(user.getUserId()));

		return Jwts.builder()
			.setSubject(userId)
			.setIssuedAt(now) // 발급 시간 설정
			.expiration(expiryDate) // 만료 시간 설정
			.signWith(getSigningKey()) // SecretKey 적용
			.compact(); // 토큰 생성
	}

	// JWT 토큰에서 사용자 정보 추출
	public String extractUserId(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();

		String jwtUserId = claims.getSubject();
		if (jwtUserId == null || jwtUserId.isEmpty()) {
			throw new BusinessException(CustomErrorCode.INVALID_TOKEN);
		}

		try {
			return String.valueOf(hashIdUtil.decode(jwtUserId));
		} catch (Exception e) {
			log.error("Failed to decode JWT userId: {}", jwtUserId, e);
			throw new BusinessException(CustomErrorCode.INVALID_TOKEN);
		}
	}

	// JWT 토큰 유효성 검사
	public boolean isTokenExpired(String token) {
		Date expiration = Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration();
		return expiration.before(new Date());
	}

	// JWT 토큰의 유효성 검사
	public boolean isTokenValid(String token) {
		try {
			Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token);
			return !isTokenExpired(token);
		} catch (ExpiredJwtException e) {
			log.warn("Token expired: {}", e.getMessage());
			return false;
		} catch (MalformedJwtException e) {
			log.warn("Invalid JWT token format: {}", e.getMessage());
			return false;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
			return false;
		} catch (Exception e) {
			log.error("Unexpected token validation error: {}", e.getMessage());
			return false;
		}
	}
}
