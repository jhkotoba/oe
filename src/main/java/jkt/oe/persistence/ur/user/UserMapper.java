package jkt.oe.persistence.ur.user;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.login.model.response.LoginResponse;

@Component
public class UserMapper {
	
	public LoginResponse findUser(UserEntity userEntity) {
		
        return LoginResponse.builder()
        	.userNo(userEntity.getUserNo())
        	.build();
    }
}
