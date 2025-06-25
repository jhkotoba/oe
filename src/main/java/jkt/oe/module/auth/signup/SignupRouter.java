package jkt.oe.module.auth.signup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SignupRouter {

	@Bean
	protected RouterFunction<ServerResponse> signupFormRouter(){
		
		return RouterFunctions.route(RequestPredicates.GET("/signup"), 
						request -> ServerResponse.ok().render("view/signup/signup.html"));
	}
}
