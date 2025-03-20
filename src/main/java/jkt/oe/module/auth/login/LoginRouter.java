package jkt.oe.module.auth.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LoginRouter {

	@Bean
	protected RouterFunction<ServerResponse> sessionRouter(LoginHandler loginHandler){
		
		return RouterFunctions			
			//로그인 프로세스
			.route(RequestPredicates.POST("/login/process")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), loginHandler::loginProcess);
			//세션정보 조회
//			.andRoute(RequestPredicates.POST("/api/member/getSession")
//				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), loginHandler::getSession)
			
	}
}
