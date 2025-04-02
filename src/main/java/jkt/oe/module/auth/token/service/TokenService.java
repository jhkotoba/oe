package jkt.oe.module.auth.token.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import jkt.oe.config.security.RsaKeyProvider;
import jkt.oe.module.auth.token.model.data.TokenCreateData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 
 */
@Service
@RequiredArgsConstructor
public class TokenService {	    
	
	/**
	 * AccessToken의 유효 시간(초)
	 */
	@Value("${custom.jwt.expiration}")
    private long expiration;
	
	/**
	 * RSA 키 제공자 컴퍼넌트
	 */
	private final RsaKeyProvider rsaKeyProvider;
	
	/**
	 * 사용자 정보를 기반으로 JWT AccessToken을 생성, 이를 쿠키로 반환
	 * @param data
	 * @return
	 */
	public Mono<ResponseCookie> generateAccessToken(TokenCreateData data) {
        
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.expiration));
        
        // 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	String token = Jwts.builder()
                .claim("userNo", data.getUserNo())
                .claim("userId", data.getUserId())
                .expiration(expiration)	// 만료 시간
                .issuedAt(Date.from(now)) // 발급 시간
                .signWith(rsaKeyProvider.getPrivateKey(), Jwts.SIG.PS256) // RSA 개인키로 서명 (PS256)
                .compact();
        	
        	return ResponseCookie.from("accessToken", token)
	          .httpOnly(true)
	          .secure(false)
	          .sameSite("Lax")
	          .path("/")
	          .maxAge(Duration.ofHours(1))
	          .build();
        });
	}
}
