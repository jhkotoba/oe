package jkt.oe.module.auth.handler;
import java.util.Map;
import java.util.UUID;

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
import jkt.oe.module.auth.model.data.RefreshTokenCreateData;
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
	    
	    return serverRequest.bodyToMono(LoginRequest.class)
			.flatMap(request -> 
		        // 사용자 정보를 조회
		        loginService.findUser(request)
		        // 사용자 검증
		        .flatMap(user -> loginService.confirmUser(request, user))
		    )
			// 토큰 생성 및 응답값 생성
		    .flatMap(user -> Mono.zip(
					// Access 토큰 생성 - tuple1
					tokenService.generateAccessToken(user.getUserNo(), UUID.randomUUID()),
					// Refresh 토큰 생성 - tuple2
					tokenService.generateRefreshToken(user.getUserNo(), UUID.randomUUID(), UUID.randomUUID()),
					// Response 객체 생성 - tuple3
					mapper.convertLoginProcessResponse(user),
					// 사용자 정보 - tuple4
					Mono.just(user)
				)
			)
		    // 리프레시 토큰 레디스 저장
		    .flatMap(tuple -> redisService.storeRefreshToken(tuple.getT4().getUserNo(), tuple.getT2())
	    		// 응답 데이터 전달
	    		.flatMap(bool -> Mono.zip(
    					// 엑세스 토큰 쿠키 생성
						tokenService.generateAccessTokenCookie(tuple.getT1()),
						// 리플레시 토큰 쿠키 생성
						tokenService.generateRefreshTokenCookie(tuple.getT2()),
						// 응답값 전송
						Mono.just(tuple.getT3())
					)
	    		)
		    )
		    // HTTP 응답처리
		    .flatMap(tuple -> ServerResponse.ok()
		            .contentType(MediaType.APPLICATION_JSON)
		            // 엑세스 토큰 쿠키
		            .cookie(tuple.getT1())
		            // 리플레시 토큰 쿠키
		            .cookie(tuple.getT2())
		            // 로그인 사용자 정보
		            .bodyValue(tuple.getT3())
		    )
		    // 오류 예외처리
			.onErrorResume(LoginException.class, ex -> ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Map.of(
						ResponseConst.MESSAGE, ex.getReason().getMessage(),
						ResponseConst.CODE, ex.getReason().getCode()
	                ))
			);
	}
}
