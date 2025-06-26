package jkt.oe.module.auth.model.request;

import lombok.Getter;

/**
 * 가입 요청 정보를 담는 DTO 클래스
 */
@Getter
public class SignupRequest {

	/**
     * 사용자 아이디
     */
	private String userId;
	
	/**
	 * 이메일
	 */
	private String email;
	
	/**
     * 사용자 비밀번호
     */
	private String password;
}
