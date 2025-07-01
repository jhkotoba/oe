package jkt.oe.module.auth.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.module.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenHandler {
	
	private final TokenService tokenService;
	
	public Mono<ServerResponse> check(ServerRequest request){
		
		// Authorization 헤더에서 토큰 추출
		String authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);	      
		
		return tokenService.validateAndGetClaims(authHeader.substring(7))
			.flatMap(claims -> {
				return ServerResponse.ok()
		                .contentType(MediaType.APPLICATION_JSON)
		                .bodyValue("OK");
	      });
	}
}
