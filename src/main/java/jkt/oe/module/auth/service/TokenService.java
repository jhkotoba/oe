package jkt.oe.module.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jkt.oe.config.constant.CookieConst;
import jkt.oe.config.constant.TokenConst;
import jkt.oe.config.security.RsaKeyProvider;
import jkt.oe.infrastructure.redis.data.StoreRefreshTokenData;
import jkt.oe.module.auth.exception.TokenException;
import jkt.oe.module.auth.model.data.AccessTokenCreateData;
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
	public Mono<String> generateRefreshToken(Long userNo){
		
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.refreshExpiration));
		
		// 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	return Jwts.builder()
        		.subject(userNo.toString())
                .claim("userNo", userNo)
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
	public Mono<String> generateAccessToken(AccessTokenCreateData data) {        
		// 현재 시간
        Instant now = Instant.now();
        // 토큰 만료 시간 계산
        Date expiration = Date.from(now.plusSeconds(this.accessExpiration));
        
        // 비동기적으로 JWT 생성 및 쿠키 생성 수행
        return Mono.fromCallable(() -> {
        	
        	// JWT 토큰 생성 (user 정보와 만료시간 포함)
        	return Jwts.builder()
        		.subject(data.getUserNo().toString())
                .claim("userNo", data.getUserNo())
                .claim("userId", data.getUserId())
                .expiration(expiration)	// 만료 시간
                .issuedAt(Date.from(now)) // 발급 시간
                .signWith(rsaKeyProvider.getPrivateKey(), Jwts.SIG.PS256) // RSA 개인키로 서명 (PS256)
                .compact();
        }).subscribeOn(Schedulers.parallel());
	}
	
//	public Mono<ServerResponse> refreshTokenFlow(ServerRequest request, TokenException ex) {
//		// 만료된 토큰에서 클레임(Subject=userNo) 추출
//	    ExpiredJwtException cause = (ExpiredJwtException) ex.getCause();
//	    Claims expiredClaims = cause.getClaims();
//	    String userNo = expiredClaims.getSubject();
//
//	    // IP 주소 (Optional 처리)
//	    String ip = request.remoteAddress()
//	        .map(addr -> addr.getAddress().getHostAddress())
//	        .orElse("");
//
//	    // User-Agent
//	    String userAgent = request.headers().firstHeader(HttpHeaders.USER_AGENT);
//
//	    // StoreRefreshTokenData 구성 후 Redis 조회
//	    StoreRefreshTokenData storeData = StoreRefreshTokenData.builder()
//	        .userNo(Long.parseLong(userNo))
//	        .ip(ip)
//	        .userAgent(userAgent)
//	        .build();
//
//	    return redisService.getRefreshToken(storeData)
//	        .switchIfEmpty(Mono.error(new TokenException(TokenException.Reason.ILLEGAL_ARGUMENT)))
//	        // 리프레시 토큰 검증
//	        .flatMap(refreshToken -> tokenService.validateAndGetClaims(refreshToken))
//	        // 검증 OK → 새 액세스 토큰 발급
//	        .flatMap(claims -> {
//	            String newAccessToken = tokenService.generateAccessToken(claims);
//	            return ServerResponse.ok()
//	                .contentType(MediaType.APPLICATION_JSON)
//	                .bodyValue(Map.of("accessToken", newAccessToken));
//	        })
//	        // 리프레시 토큰까지 실패 시 401
//	        .onErrorResume(e ->
//	            ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
//	        );
//    } 
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public Mono<ResponseCookie> generateAccessTokenCookie(String token) {
        
		return Mono.just(ResponseCookie.from(TokenConst.ACCESS_TOKEN, token)
	          .httpOnly(true)
	          .secure(true)
	          .sameSite(CookieConst.LAX)
	          .path("/")
	          .maxAge(Duration.ofSeconds(this.accessExpiration))
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
	
