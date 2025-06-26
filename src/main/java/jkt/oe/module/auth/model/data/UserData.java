package jkt.oe.module.auth.model.data;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보 데이터 객체
 */
@Getter
@Setter
@Builder
public class UserData {
	
	/**
	 * 사용자 번호
	 */
	private Long userNo;
	
	/**
	 * 사용자 아이디
	 */
	private String userId;
	
	/**
	 * 사용자 아이디
	 */
	private String email;
	
	/**
	 * 비밀번호
	 */
	private String password;
	
	/**
	 * 사용여부
	 */
	private String useYn;
	
	/**
	 * 등록일시
	 */
	private LocalDateTime insDttm;
	
	/**
	 * 솔트
	 */
	private String salt;
}
