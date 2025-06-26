package jkt.oe.persistence.ur.user;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.model.data.UserData;

/**
 * Entity 객체를 도메인 모델 객체로 변환하는 역할
 */
@Component
public class UserMapper {
	
	/**
	 * 주어진 UserEntity 객체를 기반으로 UserData 객체를 생성
	 * 
	 * @param userEntity - 데이터베이스에서 조회한 UserEntity 객체
	 * @return UserData - 도메인 모델로 변환된 서비스 데이터 객체
	 */
	public UserData findUser(UserEntity userEntity) {
		
        return UserData.builder()
        	.userNo(userEntity.getUserNo())
        	.userId(userEntity.getUserId())
        	.password(userEntity.getPassword())
        	.useYn(userEntity.getUseYn())
        	.salt(userEntity.getSalt())
        	.build();
    }
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public UserEntity saveUser(UserData user) {
        return UserEntity.builder()
    		.userId(user.getUserId())
    		.email(user.getEmail())
    		.password(user.getPassword())
    		.useYn(user.getUseYn())
    		.insDttm(user.getInsDttm())
    		.salt(user.getSalt())
    		.build();
    }
}
