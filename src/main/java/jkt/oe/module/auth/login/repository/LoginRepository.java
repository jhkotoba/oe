package jkt.oe.module.auth.login.repository;

import jkt.oe.module.auth.login.model.response.LoginResponse;
import reactor.core.publisher.Mono;

public interface LoginRepository {
	
	public Mono<LoginResponse> findByUserId(String userId);
}
