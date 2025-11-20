package jkt.oe.module.auth.model.request;

import lombok.Getter;

/**
 * 로그인 요청 정보를 담는 DTO 클래스
 */
@Getter
@Deprecated
public class LoginRequest {

	/**
     * 사용자 아이디
     */
	private String loginId;
	
	/**
     * 사용자 비밀번호
     */
	private String password;
}
