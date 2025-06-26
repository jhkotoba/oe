package jkt.oe.module.auth.handler;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.config.constant.HeaderConst;
import jkt.oe.config.constant.ResponseConst;
import jkt.oe.config.util.RequestUtil;
import jkt.oe.infrastructure.redis.data.StoreRefreshTokenData;
import jkt.oe.infrastructure.redis.service.RedisService;
import jkt.oe.module.auth.exception.LoginException;
import jkt.oe.module.auth.handler.mapper.LoginHandlerMapper;
import jkt.oe.module.auth.model.data.AccessTokenCreateData;
import jkt.oe.module.auth.model.request.LoginRequest;
import jkt.oe.module.auth.service.LoginService;
import jkt.oe.module.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 로그인 요청을 처리하는 핸들러 객체
 */
@Component
@RequiredArgsConstructor
public class LoginHandler {
	
	/**
	 * 로그인 관련 비즈니스 로직을 수행하는 서비스
	 */
	private final LoginService loginService;
	
	/**
	 * jwt 토큰 관련 비즈니스 로직을 수행하는 서비스
	 */
	private final TokenService tokenService;
	
	/**
	 * Redis 관련 서비스
	 */
	private final RedisService redisService;
	
	/**
	 * 로그인 관련 응답 객체 변환 Mapper
	 */
	private final LoginHandlerMapper mapper;	
	
	/**
	 * 로그인 프로세스를 처리하는 핸들러 메소드
	 * @param request - 클라이언트로부터의 로그인 요청을 나타내는 ServerRequest
	 * @return Mono<ServerResponse> - 클라이언트에 전송할 비동기 HTTP 응답
	 */
	public Mono<ServerResponse> loginProcess(ServerRequest serverRequest){
		
		// IP 주소 추출
		String ip = RequestUtil.getClientIp(serverRequest);
		
		// User-Agent 헤더 추출
	    String userAgent = serverRequest.headers().firstHeader(HeaderConst.USER_AGENT);
		
		return serverRequest.bodyToMono(LoginRequest.class)
			.flatMap(request -> 
		        // 사용자 정보를 조회
		        loginService.findUser(request)
		        	// 사용자 검증
		            .flatMap(user -> loginService.confirmUser(request, user))
		    ).flatMap(user -> Mono.zip(
					// Access 토큰 생성 - tuple1
					tokenService.generateAccessToken(AccessTokenCreateData.of(user.getUserNo(), user.getUserId())),
					// Response 객체 생성 - tuple2
					mapper.convertLoginProcessResponse(user),
					// Refresh 토큰 생성 - tuple3
					tokenService.generateRefreshToken(user.getUserNo()),
					// 사용자 정보 - tuple4
					Mono.just(user)
				)
			)			
			// RefreshToken redis 저장
			.flatMap(tuple -> redisService.storeRefreshToken(StoreRefreshTokenData.builder()
		        		.userNo(tuple.getT4().getUserNo())
		        		.refreshToken(tuple.getT3())
		        		.ip(ip)
		        		.userAgent(userAgent)
		        		.build()
				)
				// 응답 데이터 전달
				.then(Mono.zip(
					// ResponseCookie 객체로 생성
					tokenService.generateAccessTokenCookie(tuple.getT1()),
					// 응답값 전송
					Mono.just(tuple.getT2())
				))
			)
			// HTTP 응답처리
			.flatMap(tuple -> ServerResponse.ok()
		            .contentType(MediaType.APPLICATION_JSON)
		            .cookie(tuple.getT1())
		            .bodyValue(tuple.getT2())
			)
			// 오류 예외처리
			.onErrorResume(LoginException.class, ex -> {				
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Map.of(
							ResponseConst.MESSAGE, ex.getReason().getMessage(),
							ResponseConst.CODE, ex.getReason().getCode()
		                ));
			});
	}
}
