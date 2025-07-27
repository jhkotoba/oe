package jkt.oe.module.auth.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.module.auth.handler.TokenHandler;

@Configuration
public class TokenRouter {

	@Bean
	protected RouterFunction<ServerResponse> tokenProcessRouter(TokenHandler tokenHandler){
		
		return RouterFunctions
			.route(RequestPredicates.POST("/token/refresh")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), tokenHandler::refresh);			
	}
}
