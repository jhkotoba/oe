package jkt.oe.module.auth.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.config.constant.ResponseConst;
import jkt.oe.module.auth.exception.SignupException;
import jkt.oe.module.auth.model.request.SignupRequest;
import jkt.oe.module.auth.service.SignupService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SignupHandler {
	
	private final SignupService signupService;

	/**
	 * 
	 * @param serverRequest
	 * @return
	 */
	public Mono<ServerResponse> signupProcess(ServerRequest serverRequest){
		
		return serverRequest.bodyToMono(SignupRequest.class)
			.flatMap(request ->
		      signupService.existsByUserId(request.getUserId())
		        // existsByUserId 통과 후 saveUser() Mono를 직접 리턴
		        .then(signupService.saveUser(request))
		    )			
			// HTTP 응답처리
			.flatMap(v -> ServerResponse.ok()
		            .contentType(MediaType.APPLICATION_JSON)		            
		            .bodyValue("OK")
			)
			// 오류 예외처리
			.onErrorResume(SignupException.class, ex -> {
				ex.printStackTrace();
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Map.of(
							ResponseConst.MESSAGE, ex.getReason().getMessage(),
							ResponseConst.CODE, ex.getReason().getCode()
		                ));
			});
	}
}
