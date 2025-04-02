package jkt.oe.module.auth.login.handler;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import jkt.oe.module.auth.login.exception.LoginException;
import jkt.oe.module.auth.login.model.request.LoginRequest;
import jkt.oe.module.auth.login.model.response.LoginResponse;
import jkt.oe.module.auth.login.service.LoginService;
import jkt.oe.module.auth.token.model.data.TokenCreateData;
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
		    ).flatMap(user -> Mono.zip(
		    		
					// 접근 토큰 생성
					tokenService.generateAccessToken(TokenCreateData.of(user.getUserNo(), user.getUserId())),
					//
					mapper.convertLoginProcessResponse(user)
				)
			)
//			.map(tuple -> {
//				//return ServerResponse.ok();
////				.cookie(jwtCookie)
//				return tuple.getT1();
//				
//			})
			//.map(tuple -> mapper.convertLoginProcessResponse(tuple.getT1(), tuple.getT2()))

			// 응답객체로 변환
			//.flatMap(mapper::convertLoginProcess)
			// HTTP 응답처리
			.flatMap(tuple -> ServerResponse.ok()
		            .contentType(MediaType.APPLICATION_JSON)
		            .cookie(tuple.getT1())
		            .bodyValue(tuple.getT2())
			)
//			.flatMap(response -> ServerResponse.ok()
//					.contentType(MediaType.APPLICATION_JSON)
//					.bodyValue(response)
//					)
			// 오류 예외처리
			.onErrorResume(LoginException.class, ex -> {				
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Map.of(
		                	"message", ex.getReason().getMessage(),
		                	"code", ex.getReason().getCode()
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
