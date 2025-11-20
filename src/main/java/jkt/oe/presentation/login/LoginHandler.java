package jkt.oe.presentation.login;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.application.authentication.login.port.in.LoginUseCase;
import jkt.oe.presentation.login.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginHandler {

       
    private final LoginMapper loginMapper;
    
    private final LoginUseCase loginUseCase;

	public Mono<ServerResponse> loginProcess(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(LoginRequest.class)
            .map(loginMapper::toCommand)
            .flatMap(loginUseCase::login)
            .flatMap(request -> {
                // 임시
                return ServerResponse.ok().build();
            });

                         
    }
}
