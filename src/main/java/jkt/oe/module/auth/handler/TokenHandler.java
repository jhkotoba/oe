package jkt.oe.module.auth.handler;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.jsonwebtoken.ExpiredJwtException;
import jkt.oe.config.constant.ResponseConst;
import jkt.oe.infrastructure.redis.data.StoreRefreshTokenData;
import jkt.oe.infrastructure.redis.service.RedisService;
import jkt.oe.module.auth.exception.TokenException;
import jkt.oe.module.auth.model.data.AccessTokenCreateData;
import jkt.oe.module.auth.service.LoginService;
import jkt.oe.module.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenHandler {
	
	private final TokenService tokenService;
	private final RedisService redisService;
	private final LoginService loginService;
	
	public Mono<ServerResponse> check(ServerRequest request){
		
		// Authorization 헤더에서 토큰 추출
		String authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);
		String accessToken = authHeader != null && authHeader.startsWith("Bearer ")
	            ? authHeader.substring(7)
	            : "";
		
		return tokenService.validateAndGetClaims(accessToken)
			.doOnError(e -> e.printStackTrace())
			.onErrorResume(TokenException.class, ex -> {
				
				TokenException e = (TokenException) ex;				
				if (e.getReason() == TokenException.Reason.EXPIRED) {
					
					// expiredClaims → userNo 추출
	                ExpiredJwtException cause = (ExpiredJwtException) ex.getCause();
	                String userNo = cause.getClaims().getSubject();
	                // IP/User-Agent 조회
	                String ip = request.remoteAddress()
	                    .map(addr -> addr.getAddress().getHostAddress()).orElse("");
	                String ua = request.headers().firstHeader(HttpHeaders.USER_AGENT);

	                StoreRefreshTokenData data = StoreRefreshTokenData.builder()
	                    .userNo(Long.parseLong(userNo)).ip(ip).userAgent(ua).build();

	                // 리프레시 토큰 꺼내서 validateAndGetClaims로 재검증
	                return redisService.getRefreshToken(data)
	                    .switchIfEmpty(Mono.error(new TokenException(TokenException.Reason.ILLEGAL_ARGUMENT)))
	                    .flatMap(tokenService::validateAndGetClaims);
				}
				
				return Mono.error(ex);				
			})
			// DB 사용자 확인/조회 
			.flatMap(claims -> {
				Long userNo = Long.parseLong(claims.getSubject());
				return loginService.findUser(userNo);
			})
			// 엑세스 토큰 생성
			.flatMap(user -> tokenService.generateAccessToken(AccessTokenCreateData.of(user.getUserNo(), user.getUserId())))
			// 엑세스 토큰 쿠키 생성
			.flatMap(tokenService::generateAccessTokenCookie)
			// 응답/ 엑세스 토큰 쿠키저장
			.flatMap(cookie -> {
				return ServerResponse.ok()
		                .contentType(MediaType.APPLICATION_JSON)
		                .cookie(cookie)
		                .bodyValue("OK");
			//})
			}).doOnError(e -> e.printStackTrace())
			// 토큰 만료 케이스: 레디스에서 리프레시 토큰 조회 후 재발급 흐름으로 분기
			.onErrorResume(TokenException.class, ex -> {				
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Map.of(
							ResponseConst.MESSAGE, ex.getReason().getMessage(),
							ResponseConst.CODE, ex.getReason().getCode()
		                ));
			});
			
	}
	
//	private Mono<ServerResponse> refreshTokenFlow(ServerRequest request, TokenException ex) {
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
}
