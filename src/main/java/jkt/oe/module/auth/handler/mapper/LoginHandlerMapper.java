package jkt.oe.module.auth.handler.mapper;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.model.data.UserData;
import jkt.oe.module.auth.model.response.LoginResponse;
import reactor.core.publisher.Mono;

@Component
public class LoginHandlerMapper {

	public Mono<LoginResponse> convertLoginProcessResponse(UserData data) {
		return Mono.just(LoginResponse.builder()
				.userId(data.getUserId())
				.build()
			);
	}
}
