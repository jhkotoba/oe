// package jkt.oe.module.auth.router;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.MediaType;
// import org.springframework.web.reactive.function.server.RequestPredicates;
// import org.springframework.web.reactive.function.server.RouterFunction;
// import org.springframework.web.reactive.function.server.RouterFunctions;
// import org.springframework.web.reactive.function.server.ServerResponse;

// import jkt.oe.module.auth.handler.SignupHandler;

// @Configuration
// public class SignupRouter {

// 	@Bean
// 	protected RouterFunction<ServerResponse> signupFormRouter(){		
// 		return RouterFunctions.route(RequestPredicates.GET("/signup"), 
// 						request -> ServerResponse.ok().render("view/signup/signup.html"));
// 	}
	
// 	@Bean
// 	protected RouterFunction<ServerResponse> signupProcessRouter(SignupHandler signupHandler){		
// 		return RouterFunctions
// 			// 가입 프로세스
// 			.route(RequestPredicates.POST("/signup/process")
// 				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), signupHandler::signupProcess);			
// 	}
// }
