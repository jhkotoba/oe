package jkt.oe.module.auth.login.service;

import javax.security.auth.login.LoginException;

import org.springframework.stereotype.Service;

import jkt.oe.module.auth.login.model.request.LoginRequest;
import jkt.oe.module.auth.login.model.response.LoginResponse;
import jkt.oe.module.auth.login.repository.LoginRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class LoginService {
	
	private LoginRepository loginRepository;
	
	public Mono<LoginResponse> findUser(LoginRequest model){
		
		return loginRepository.findByUserId(model.getUserId())
                .switchIfEmpty(Mono.error(new LoginException("User not found")));
	}
	
	public void confirmUser() {
		
	}

}
