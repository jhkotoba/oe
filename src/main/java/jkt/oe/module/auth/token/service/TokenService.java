package jkt.oe.module.auth.token.service;

import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jkt.oe.module.auth.token.model.data.TokenCreateData;
import reactor.core.publisher.Mono;

@Service
public class TokenService {	    
	
	@Value("${custom.jwt.secretKey}")
	private String jwtSecretKey;
	
	// 작업중
	public Mono<ResponseCookie> generateAccessToken(TokenCreateData data) {
		
		return Mono.fromCallable(() -> {
			String token = Jwts.builder()
	    		.claim("userNo", data.getUserNo())
	    		.claim("userId", data.getUserId())    		
	    		.expiration(new Date(System.currentTimeMillis() + 1000000L))
	    		.signWith(Keys.hmacShaKeyFor(this.jwtSecretKey.getBytes()))
	    		.compact();
			
			return ResponseCookie.from("accessToken", token)
	          .httpOnly(true)
	          .path("/")
	          .maxAge(Duration.ofHours(1))
	          .build();
		});
	}
	
	// 작업중
	public Mono<ResponseCookie> generateRefreshToken(TokenCreateData data) {
		
		return Mono.fromCallable(() -> {
			String token = Jwts.builder()
					.claim("userNo", data.getUserNo())
					.claim("userId", data.getUserId())    		
					.expiration(new Date(System.currentTimeMillis() + 1000000L))
					.signWith(Keys.hmacShaKeyFor(this.jwtSecretKey.getBytes()))
					.compact();
			
			return ResponseCookie.from("refreshToken", token)
					.httpOnly(true)
					.path("/")
					.maxAge(Duration.ofHours(1))
					.build();
		});
	}
	
	public Claims validateToken(String token) {
		return Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(this.jwtSecretKey.getBytes()))
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

}
