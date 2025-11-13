package jkt.oe.module.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jkt.oe.config.constant.CookieConst;
import jkt.oe.config.constant.OeConst;
import jkt.oe.config.security.RsaKeyProvider;
import jkt.oe.module.auth.exception.TokenException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 
 */
@Service
@RequiredArgsConstructor
public class TokenService {    
	
	@Value("${spring.profiles.active}")
	private String profilesActive;	
	
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
	 * RefreshToken 생성 (rotated=true 전제: fid를 유지, jti는 매번 신규)
	 * @param memberId 사용자 번호
	 * @param jti RT 고유 ID
	 * @param fid RT family ID
	 * @return Mono<String> RT
	 */
	public Mono<String> generateRefreshToken(Long memberId, UUID jti, UUID fid){
		
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.refreshExpiration));
		
		// 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	return Jwts.builder()
        		.issuer("oe-auth-" + profilesActive)
        		.subject(memberId.toString())
        		.id(jti.toString())
        		.audience()
	    			.add("gateway")
	    			.add("auth")
	    			.and()
        		.claim("type", OeConst.REFRESH_TOKEN)
        		.claim("role", "") // TODO 추후추가예정 - 역활
        		.claim("scope", "") // TODO 추후추가예정 - 권한
        		.claim("fid", fid) 
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
	public Mono<String> generateAccessToken(Long memberId, UUID jti) {        
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.accessExpiration));
                
        // 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	return Jwts.builder()
        		.issuer("oe-auth-" + profilesActive)
        		.subject(memberId.toString())
        		.id(jti.toString())
        		.audience()
        			.add("gateway")
        			.add("auth")
        			.and()
        		.claim("type", OeConst.ACCESS_TOKEN)
        		.claim("role", "") // TODO 추후추가예정 - 역활
        		.claim("scope", "") // TODO 추후추가예정 - 권한
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
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public Mono<ResponseCookie> generateRefreshTokenCookie(String token) {
        
		return Mono.just(
				ResponseCookie.from(OeConst.REFRESH_TOKEN, token)
				.httpOnly(true)
		        .secure(true)
		        .sameSite(CookieConst.STRICT)
		        .path("/")
		        .maxAge(Duration.ofSeconds(this.refreshExpiration))
		        .build());
	}	

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
	
	public Mono<Claims> validate(String token){
		if (token == null || token.isBlank()) {
	        return Mono.error(new TokenException(TokenException.Reason.SIGNATURE_INVALID));
	    }
		
		return Mono.fromCallable(() -> {			
			
			Jws<Claims> jws = Jwts.parser()
					.requireIssuer("oe-auth-" + profilesActive)
					.clockSkewSeconds(5)
					.verifyWith(this.rsaKeyProvider.getPublicKey())
					.build()
					.parseSignedClaims(token);
			
			return jws.getPayload();
			
			}).doOnError(e -> e.printStackTrace())
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
	
	
	public Mono<Claims> validateAndGetClaims(String token){
		
		
//		{
//			  "iss": "oe-auth-local",
//			  "sub": "5",
//			  "aud": ["gateway","auth", "api-dashboard"],     // 어디서만 받는지(서비스)
//			  "type": "access",
//			  "roles": ["ROLE_USER","ROLE_MANAGER"],  // 신분/역할
//			  "scope": "student:read gift:write",     // 세부 동작 권한
//			  "exp": 1730600180,
//			  "iat": 1730600000
//			}
		
		if (token == null || token.isBlank()) {
	        return Mono.empty();
	    }
		
		return Mono.fromCallable(() -> {			
			
			Jws<Claims> jws = Jwts.parser()
					.requireIssuer("oe-auth-" + profilesActive)
					.clockSkewSeconds(5)
					.verifyWith(this.rsaKeyProvider.getPublicKey())
					.build()
					.parseSignedClaims(token);
			
			// 헤더 체크
			JwsHeader header = jws.getHeader();
			String alg = header.getAlgorithm();
	        if (!"PS256".equals(alg)) { // 허용 알고리즘 고정
	            throw new TokenException(TokenException.Reason.UNSUPPORTED);
	        }
	        
	        return jws.getPayload();
			
	        // 수신자 체크
//	        Claims claims = jws.getPayload();
//	        Set<String> aud = claims.getAudience();
//			
//	        if (aud == null || !(aud.contains("gateway") || aud.contains("auth"))) {
//	            throw new TokenException(TokenException.Reason.ILLEGAL_ARGUMENT);
//	        }
	        
//			return claims;
			
		}).doOnError(e -> e.printStackTrace())
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
	
//	
}
	
