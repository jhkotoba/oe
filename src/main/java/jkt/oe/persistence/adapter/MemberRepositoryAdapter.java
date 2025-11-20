package jkt.oe.persistence.adapter;

import org.springframework.stereotype.Repository;

import jkt.oe.module.auth.model.data.MemberData;
import jkt.oe.module.auth.repository.LoginRepository;
import jkt.oe.module.auth.repository.SignupRepository;
import jkt.oe.persistence.mapper.MemberMapper;
import jkt.oe.persistence.repository.MemberEntityRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 도메인 계층의 사용자 관련 Repository 인터페이스를 구현하는 어댑터 클래스
 */
@Repository
@AllArgsConstructor
@Deprecated
public class MemberRepositoryAdapter implements LoginRepository, SignupRepository {
	
	/**
	 * 사용자 정보 조회 Entity Repository 인터페이스
	 */
	private final MemberEntityRepository memberEntityRepository;
	
	/**
	 * 응답객체 변환
	 */
	private final MemberMapper memberMapper;
	
	/**
	 * 사용자 ID를 이용해 DB에서 UserEntity를 조회, LoginResponse로 변환하여 반환
	 * 
	 * @param loginId 조회할 사용자 ID
	 * @return 조회된 UserEntity를 UserData로 매핑한 Mono를 반환하며, 해당 사용자가 없으면 빈 Mono를 반환
	 */
	@Override
	public Mono<MemberData> findByLoginId(String loginId) {
		return memberEntityRepository.findByLoginId(loginId)
			.map(memberMapper::findMember);
	}
	
	@Override
	public Mono<MemberData> findMember(Long memberId) {		
		return memberEntityRepository.findById(memberId)
			.map(memberMapper::findMember);
	}
	
	/**
	 * 주어진 로그인 ID(loginId)를 가진 사용자가 데이터베이스에 존재하는지 확인
	 * 
     * @param userId 조회할 로그인 ID
     * @return 사용자가 존재하면 Mono<Boolean>이 true, 그렇지 않으면 false 를 발행
	 */
	@Override
	public Mono<Boolean> existsByLoginId(String loginId) {
		return memberEntityRepository.existsByLoginId(loginId);
	}
	
	/**
	 * 
	 */
	@Override
	public Mono<MemberData> saveMember(MemberData member) {
		return memberEntityRepository
			    .save(memberMapper.saveMember(member))
			    .map(memberMapper::findMember);
	}	
}
