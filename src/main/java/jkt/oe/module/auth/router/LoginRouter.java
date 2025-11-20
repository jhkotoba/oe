package jkt.oe.module.auth.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.module.auth.handler.LoginHandler;

/**
 * 로그인 관련 Routing 처리 객체
 */
@Configuration
@Deprecated
public class LoginRouter {

	@Bean
	protected RouterFunction<ServerResponse> loginFormRouter(){		
		return RouterFunctions.route(RequestPredicates.GET("/login"), 
				request -> ServerResponse.ok().render("view/login/login.html"));
	}
	
	/**
	 * 로그인 프로세스에 대한 라우트 함수를 생성
	 * @param loginHandler - 로그인 요청을 처리하는 핸들러
	 * @return RouterFunction<ServerResponse> 클라이언트 요청을 처리할 라우트 함수
	 */
	@Bean
	protected RouterFunction<ServerResponse> loginProcessRouter(LoginHandler loginHandler){
		
		return RouterFunctions
			//로그인 프로세스
			.route(RequestPredicates.POST("/login/process")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), loginHandler::loginProcess);			
	}
}
