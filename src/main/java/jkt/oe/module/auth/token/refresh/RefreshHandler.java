package jkt.oe.module.auth.token.refresh;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.module.auth.token.refresh.model.request.RefreshRequest;
import jkt.oe.module.auth.token.refresh.model.response.RefreshResponse;
import jkt.oe.module.auth.token.refresh.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RefreshHandler {

	private final RefreshTokenService refreshTokenService;    

    public Mono<ServerResponse> refreshToken(ServerRequest request) {
        return request.bodyToMono(RefreshRequest.class)
                .flatMap(refreshRequest ->
                    refreshTokenService.validateAndRefreshToken(
                    		"test", "test"
                            //refreshRequest.getUsername(), 
                            //refreshRequest.getRefreshToken()
                    )
                    .flatMap(newToken -> 
                        ServerResponse.ok().bodyValue(new RefreshResponse(newToken))
                    )
                    .switchIfEmpty(
                        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
                    )
                );
    }
}
