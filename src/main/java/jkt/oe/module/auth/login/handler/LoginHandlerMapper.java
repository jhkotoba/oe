package jkt.oe.module.auth.login.handler;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.login.model.data.UserData;
import jkt.oe.module.auth.login.model.response.LoginResponse;
import reactor.core.publisher.Mono;

@Component
public class LoginHandlerMapper {

	public Mono<LoginResponse> convertLoginProcessResponse(UserData data) {
		return Mono.just(LoginResponse.builder()
				.userNo(data.getUserNo())
				.build()
				);
	}
}
