package jkt.oe.module.auth.login;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import jkt.oe.module.auth.login.exception.LoginException;
import jkt.oe.module.auth.login.model.request.LoginRequest;
import jkt.oe.module.auth.login.service.LoginService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class LoginHandler implements WebExceptionHandler {
	
	private final LoginService loginService;
	
	public Mono<ServerResponse> loginProcess(ServerRequest request){
		
		return request.bodyToMono(LoginRequest.class)
			.flatMap(loginService::findUser)
			//.flatMap(loginService::confirmUser)
			.flatMap(result -> ServerResponse.ok()
		            .contentType(MediaType.APPLICATION_JSON)
		            .body(BodyInserters.fromValue(result)))
			
			.onErrorResume(LoginException.class, ex -> {				
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(Map.of(
		                	"message", ex.getReason().getMessage(),
		                	"code", ex.getReason().getCode()
		                )));
			});
		
//		return request.bodyToMono(LoginModel.class)
//			//회원조회
//			.flatMap(loginService::selectUser)
//			//회원정보 체크
//			.flatMap(loginService::userConfirm)
//			//응답
//			.flatMap(result -> {
//				return ServerResponse.ok()						
//					.contentType(MediaType.APPLICATION_JSON)
//					.body(BodyInserters.fromValue(result));
//		    })
//			.onErrorResume(error -> {
//				Map<String, Object> result = new HashMap<String, Object>();
//				result.put("loginYn", Constant.Y);
//				result.put("resultCode", error.getMessage());
//				return ServerResponse.ok()						
//						.contentType(MediaType.APPLICATION_JSON)
//						.body(BodyInserters.fromValue(result));
//			});
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
