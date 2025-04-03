package jkt.oe.module.auth.login.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import jkt.oe.config.constant.ResponseConst;
import jkt.oe.infrastructure.redis.data.StoreRefreshTokenData;
import jkt.oe.infrastructure.redis.service.RedisService;
import jkt.oe.module.auth.login.exception.LoginException;
import jkt.oe.module.auth.login.model.request.LoginRequest;
import jkt.oe.module.auth.login.service.LoginService;
import jkt.oe.module.auth.token.model.data.AccessTokenCreateData;
import jkt.oe.module.auth.token.model.data.RefreshTokenCreateData;
import jkt.oe.module.auth.token.service.TokenService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 로그인 요청을 처리하는 핸들러 객체
 */
@Component
@AllArgsConstructor
public class LoginHandler implements WebExceptionHandler {
	
	/**
	 * 로그인 관련 비즈니스 로직을 수행하는 서비스
	 */
	private final LoginService loginService;
	
	/**
	 * jwt 토큰 관련 비즈니스 로직을 수행하는 서비스
	 */
	private final TokenService tokenService;
	
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
		
		// 클라이언트의 IP와 User-Agent 정보 추출
        String ip = serverRequest.remoteAddress()
            .map(addr -> addr.getAddress().getHostAddress())
            .orElse("unknown");
        String userAgent = serverRequest.headers().firstHeader("User-Agent");
		
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
					tokenService.generateRefreshToken(RefreshTokenCreateData.of(user.getUserNo(), ip, userAgent)),
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

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		// TODO Auto-generated method stub
		return null;
		
//		// 1) 예외 로그 출력
//        log.error("Global Exception Caught: ", ex);
//
//        // 2) 상태 코드 결정 (필요 시 예외 타입별로 분기)
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        if (ex instanceof ResponseStatusException) {
//            status = ((ResponseStatusException) ex).getStatusCode();
//        } 
//        // 커스텀 예외 분기 예시
//        // else if (ex instanceof MyCustomBadRequestException) {
//        //     status = HttpStatus.BAD_REQUEST;
//        // }
//
//        // 3) 응답 바디 구성
//        Map<String, Object> errorBody = new HashMap<>();
//        errorBody.put("success", false);
//        errorBody.put("status", status.value());
//        errorBody.put("error", ex.getClass().getSimpleName());
//        errorBody.put("message", ex.getMessage());
//
//        // 4) 응답 헤더/바디 설정
//        exchange.getResponse().setStatusCode(status);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//
//        // 5) JSON 변환 후 Body에 기록
//        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorBody))
//                .flatMap(bytes -> {
//                    DataBuffer buffer = exchange.getResponse()
//                        .bufferFactory()
//                        .wrap(bytes);
//                    return exchange.getResponse().writeWith(Mono.just(buffer));
//                });
//    }
	}

}
