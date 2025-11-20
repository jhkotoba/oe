package jkt.oe.module.auth.handler.mapper;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.model.data.MemberData;
import jkt.oe.module.auth.model.response.LoginResponse;
import reactor.core.publisher.Mono;

@Component
@Deprecated
public class LoginHandlerMapper {

	public Mono<LoginResponse> convertLoginProcessResponse(MemberData data) {
		return Mono.just(LoginResponse.builder()
				.memberId(data.getMemberId())
				.build()
			);
	}
}
