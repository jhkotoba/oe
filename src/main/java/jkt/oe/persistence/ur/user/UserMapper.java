package jkt.oe.persistence.ur.user;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.login.model.response.LoginResponse;

/**
 * Entity 객체를 도메인 모델 객체로 변환하는 역할
 */
@Component
public class UserMapper {
	
	/**
	 * 주어진 UserEntity 객체를 기반으로 LoginResponse 객체를 생성
	 * 
	 * @param userEntity - 데이터베이스에서 조회한 UserEntity 객체
	 * @return LoginResponse - 도메인 모델로 변환된 객체
	 */
	public LoginResponse findUser(UserEntity userEntity) {
		
        return LoginResponse.builder()
        	.userNo(userEntity.getUserNo())
        	.build();
    }
}
