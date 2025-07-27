package jkt.oe.module.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jkt.oe.config.constant.CookieConst;
import jkt.oe.config.constant.OeConst;
import jkt.oe.config.security.RsaKeyProvider;
import jkt.oe.module.auth.exception.TokenException;
import jkt.oe.module.auth.model.data.RefreshTokenCreateData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 
 */
@Service
@RequiredArgsConstructor
public class TokenService {    
	
	/**
	 * AccessToken의 유효 시간(초)
	 */
	@Value("${custom.jwt.access-expiration}")
    private Long accessExpiration;
	
	/**
	 * RefreshToken의 유효 시간(초)
	 */
	@Value("${custom.jwt.refresh-expiration}")
	private Long refreshExpiration;	
	
	/**
	 * RSA 키 제공자 컴퍼넌트
	 */
	private final RsaKeyProvider rsaKeyProvider;
	
	/**
	 * 
	 * @param userNo
	 * @return
	 */
	public Mono<String> generateRefreshToken(RefreshTokenCreateData data){
		
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.refreshExpiration));
		
		// 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	return Jwts.builder()
        		.subject(data.getUuid())
        		.claim("type", OeConst.REFRESH_TOKEN)
                .claim("userNo", data.getUserNo())
                .claim("userId", data.getUserId())
                .expiration(expiration)	// 만료 시간
                .issuedAt(Date.from(now)) // 발급 시간
                .signWith(rsaKeyProvider.getPrivateKey(), Jwts.SIG.PS256) // RSA 개인키로 서명 (PS256)
                .compact();
        }).subscribeOn(Schedulers.parallel());
	}
	
	/**
	 * 사용자 정보를 기반으로 JWT AccessToken을 생성, 이를 쿠키로 반환
	 * @param data
	 * @return
	 */
	//public Mono<ResponseCookie> generateAccessToken(AccessTokenCreateData data) {        
	public Mono<String> generateAccessToken(String uuid) {        
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.accessExpiration));
        
        // 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	return Jwts.builder()
        		.subject(uuid)
        		.claim("type", OeConst.ACCESS_TOKEN)
//        		.subject(data.getUserNo().toString())
//                .claim("userNo", data.getUserNo())
//                .claim("userId", data.getUserId())
                .expiration(expiration)	// 만료 시간
                .issuedAt(Date.from(now)) // 발급 시간
                .signWith(rsaKeyProvider.getPrivateKey(), Jwts.SIG.PS256) // RSA 개인키로 서명 (PS256)
                .compact();
        }).subscribeOn(Schedulers.parallel());
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public Mono<ResponseCookie> generateAccessTokenCookie(String token) {
        
		return Mono.just(
				ResponseCookie.from(OeConst.ACCESS_TOKEN, token)
				.httpOnly(true)
		        .secure(true)
		        .sameSite(CookieConst.STRICT)
		        .path("/")
		        .maxAge(Duration.ofSeconds(this.accessExpiration))
		        .build());
	}
	
	// TODO 쿠키 생성 메소드는 서비스를 옮겨야 할것 같음
	public Mono<ResponseCookie> generateUUIDCookie(String uuid) {
		
		return Mono.just(
				ResponseCookie.from(OeConst.UUID, uuid)
				.httpOnly(true)
				.secure(true)
				.sameSite(CookieConst.STRICT)
				.path("/")
				.maxAge(Duration.ofSeconds(this.refreshExpiration))
				.build());
	}
	

	
	public Mono<Claims> validateAndGetClaims(String token){
		return Mono.fromCallable(() -> {
			
			Jws<Claims> jws = Jwts.parser()
					.verifyWith(this.rsaKeyProvider.getPublicKey())
					.build()
					.parseSignedClaims(token);
			
			return jws.getPayload();
			
		//}).doOnError(e -> e.printStackTrace()
		})
		.subscribeOn(Schedulers.parallel())
		// 토큰 만료
        .onErrorMap(ExpiredJwtException.class, ex -> new TokenException(TokenException.Reason.EXPIRED, ex))
        // 형식 오류
        .onErrorMap(MalformedJwtException.class, ex -> new TokenException(TokenException.Reason.MALFORMED, ex))
        // 서명 검증 실패
        .onErrorMap(SignatureException.class, ex -> new TokenException(TokenException.Reason.SIGNATURE_INVALID, ex))
        // 지원하지 않는 토큰 형식
        .onErrorMap(UnsupportedJwtException.class, ex -> new TokenException(TokenException.Reason.UNSUPPORTED, ex))
        // 잘못된 인자 (null 또는 빈 문자열 등)
        .onErrorMap(IllegalArgumentException.class, ex -> new TokenException(TokenException.Reason.ILLEGAL_ARGUMENT, ex))
        // Base64 디코딩 실패 등 그 외 모든 JwtException
        .onErrorMap(JwtException.class, ex -> new TokenException(TokenException.Reason.DECODING_ERROR, ex));
	}
	
	
}
	
