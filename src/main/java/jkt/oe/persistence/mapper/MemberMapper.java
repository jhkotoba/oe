package jkt.oe.persistence.mapper;

import org.springframework.stereotype.Component;

import jkt.oe.module.auth.model.data.MemberData;
import jkt.oe.persistence.entity.MemberEntity;

/**
 * Entity 객체를 도메인 모델 객체로 변환하는 역할
 */
@Component
public class MemberMapper {
	
	/**
	 * 주어진 memberEntity 객체를 기반으로 memberData 객체를 생성
	 * 
	 * @param memberEntity - 데이터베이스에서 조회한 memberEntity 객체
	 * @return memberData - 도메인 모델로 변환된 서비스 데이터 객체
	 */
	public MemberData findMember(MemberEntity memberEntity) {
		
        return MemberData.builder()
        	.memberId(memberEntity.getMemberId())
        	.loginId(memberEntity.getLoginId())
        	.email(memberEntity.getEmail())
        	.password(memberEntity.getPassword())
        	.salt(memberEntity.getSalt())
        	.isActive(memberEntity.isActive())
        	.createdAt(memberEntity.getCreatedAt())
        	.updatedAt(memberEntity.getUpdatedAt())
        	.build();
    }
	
    /**
     * Data -> Entity
     * @param user 서비스 데이터
     * @return UserEntity
     */
	public MemberEntity saveMember(MemberData user) {
        return MemberEntity.builder()
    		.memberId(user.getMemberId())
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
