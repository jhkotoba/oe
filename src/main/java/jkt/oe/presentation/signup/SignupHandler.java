package jkt.oe.presentation.signup;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.presentation.signup.dto.SignupReqeust;
import reactor.core.publisher.Mono;

@Component
public class SignupHandler {

    public Mono<ServerResponse> signupProcess(ServerRequest serverRequest) {

        // serverRequest.bodyToMono(SignupReqeust.class)
           // .map(signupReqeust ->


        return ServerResponse.ok().build();

    }
	
}
