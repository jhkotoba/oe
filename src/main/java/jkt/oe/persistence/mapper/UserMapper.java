package jkt.oe.persistence.mapper;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.model.data.UserData;
import jkt.oe.persistence.entity.UserEntity;

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
        	.userId(userEntity.getUserId())
        	.loginId(userEntity.getLoginId())
        	.email(userEntity.getEmail())
        	.password(userEntity.getPassword())
        	.salt(userEntity.getSalt())
        	.isActive(userEntity.isActive())
        	.createdAt(userEntity.getCreatedAt())
        	.updatedAt(userEntity.getUpdatedAt())
        	.build();
    }
	
    /**
     * Data -> Entity
     * @param user 서비스 데이터
     * @return UserEntity
     */
	public UserEntity saveUser(UserData user) {
        return UserEntity.builder()
    		.userId(user.getUserId())
        	.loginId(user.getLoginId())
        	.email(user.getEmail())
        	.password(user.getPassword())
        	.salt(user.getSalt())
        	.isActive(user.isActive())
        	.createdAt(user.getCreatedAt())
        	.updatedAt(user.getUpdatedAt())
        	.build();
    }
}
