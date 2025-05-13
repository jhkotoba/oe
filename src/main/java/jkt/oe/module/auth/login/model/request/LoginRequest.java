package jkt.oe.module.auth.login.model.request;

import lombok.Getter;

/**
 * 로그인 요청 정보를 담는 DTO 클래스
 */
@Getter
public class LoginRequest {

	/**
     * 사용자 아이디
     */
	private String userId;
	
	/**
     * 사용자 비밀번호
     */
	private String password;
}
