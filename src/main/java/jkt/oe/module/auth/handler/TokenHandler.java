package jkt.oe.module.auth.handler;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jkt.oe.config.constant.ResponseConst;
import jkt.oe.config.constant.TokenConst;
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
		String uuid = request.headers().firstHeader("uuid");
		
		return tokenService.validateAndGetClaims(accessToken)
			//.doOnError(e -> e.printStackTrace())
			.onErrorResume(TokenException.class, ex -> {
				
				TokenException e = (TokenException) ex;				
				if (TokenException.Reason.EXPIRED.equals(e.getReason())) {
					
	                // 리프레시 토큰 꺼내서 validateAndGetClaims로 재검증
					return redisService.getRefreshToken(uuid)
		                    .switchIfEmpty(Mono.error(new TokenException(TokenException.Reason.ILLEGAL_ARGUMENT, ex)))
		                    .flatMap(tokenService::validateAndGetClaims);
	                
	                
	                //return Mono.zip(Mono.just(""), Mono.just(""));
				}
				return Mono.error(ex);
			})
			.flatMap(claims -> {
				String claimsType = String.valueOf(claims.get("type"));				
				
				if(TokenConst.ACCESS_TOKEN.equals(claimsType)) {
					return ServerResponse.ok()
			                .contentType(MediaType.APPLICATION_JSON).build();
				}else if(TokenConst.REFRESH_TOKEN.equals(claimsType)) {
					
					// DB 사용자 확인/조회
					return loginService.findUser(Long.parseLong(String.valueOf(claims.get("userNo"))))						
						// 기존 UUID 삭제, 신규 UUID 생성
						.flatMap(user -> {
							
							String nextUuid = UUID.randomUUID().toString();
							
							return Mono.zip(
								redisService.removeUUID(uuid),									
								tokenService.generateAccessToken(nextUuid),
								Mono.just(nextUuid)
							);
							
//							return ServerResponse.ok()
//									.contentType(MediaType.APPLICATION_JSON)
//									.build();                               
							
							
						})
						.flatMap(tuple -> {
							
							return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.build();  
							
							
						});
//						.flatMap(tuple -> {
//							if(tuple.getT1() == false) {
//								return Mono.error(new TokenException(TokenException.Reason.REFRESH_RUNTIME_ERROR));
//							}else {
//								return ServerResponse.ok()
//					                .contentType(MediaType.APPLICATION_JSON)
//					                .cookie(tuple.getT1())
//					                .cookie(tuple.getT2())
//					                .build();
//							}
//						});
//						.flatMap(tuple -> {
//							
//							return ServerResponse.ok()
//			                .contentType(MediaType.APPLICATION_JSON)
//			                .cookie(tuple.getT1())
//			                .cookie(tuple.getT2())
//			                .build();
//							
//						});
					
				}else {
					return Mono.error(new TokenException(TokenException.Reason.ILLEGAL_ARGUMENT));
				}
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
			
			
//			// DB 사용자 확인/조회 
//			.flatMap(claims -> loginService.findUser(Long.parseLong(String.valueOf(claims.get("userNo")))))
//			
//			// 기존 UUID 삭제, 신규 UUID 생성
//			.flatMap(user -> {
//				
//				return Mono.empty();
//				
//				
//				
//			});
			
			
		
//			.flatMap(user -> {
//				
//				return Mono.zip(user.getUserId(), "A");
//				
//				
//			})
			// 기존 UUID 삭제 및 신규 UUID로 갱신
//			.flatMap(user -> { 
//				
//				return Mono.just(UUID.randomUUID().toString());
//			})
			// 엑세스 토큰 및 신규 UUID 생성
			//.flatMap(uuid -> Mono.zip(tokenService.generateAccessToken(uuid), uuid))
			// 엑세스 토큰 쿠키 생성 // TODO UUID 쿠키도 추가 해야함
			//.flatMap(tokenService::generateAccessTokenCookie)
//			.flatMap(tuple -> Mono.zip(
//					tokenService.generateAccessToken(tuple.getT1()),
//					tokenService.generateUUIDCookie(tuple.getT2())
//				)
//			)
			// 응답/ 엑세스 토큰 쿠키저장
//			.flatMap(tuple -> {
//				return ServerResponse.ok()
//		                .contentType(MediaType.APPLICATION_JSON)
//		                .cookie(tuple.getT1())
//		                .cookie(tuple.getT2())
//		                .bodyValue("OK");
//			//})
//			}).doOnError(e -> e.printStackTrace())
//			// 토큰 만료 케이스: 레디스에서 리프레시 토큰 조회 후 재발급 흐름으로 분기
//			.onErrorResume(TokenException.class, ex -> {				
//				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
//					.contentType(MediaType.APPLICATION_JSON)
//					.bodyValue(Map.of(
//							ResponseConst.MESSAGE, ex.getReason().getMessage(),
//							ResponseConst.CODE, ex.getReason().getCode()
//		                ));
//			});
			
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
