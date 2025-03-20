package jkt.oe.module.auth.token.refresh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RefreshRouter {

    @Bean
    protected RouterFunction<ServerResponse> refreshRoutes(RefreshHandler authHandler) {
        return RouterFunctions.route()
                .POST("/refresh", authHandler::refreshToken)
                .build();
    }
	
}
