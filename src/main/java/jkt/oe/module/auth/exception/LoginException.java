package jkt.oe.module.auth.exception;

/**
 * 로그인 과정에서 발생할 수 있는 사용자 인증 관련 예외를 처리하기 위한 커스텀 런타임 예외 클래스
 */
public class LoginException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Reason reason;
	
	public enum Reason {
		
		/**
         * 사용자 정보가 존재하지 않을 때 발생
         * 코드: 10001
         */
		MEMBER_NOT_FOUND("10001", "존재하지 않은 사용자 입니다."),
		
		/**
         * 비밀번호가 일치하지 않을 때 발생
         * 코드: 10002
         */
		INVALID_CREDENTIALS ("10002", "비밀번호가 올바르지 않습니다.");
		
		private final String code;
		private final String message;
		
		Reason(String code, String message) {
			this.code = code; 
			this.message = message;
        }
		
		public String getCode() {
            return code;
        }
		
		public String getMessage() {
            return message;
        }
	}
	
	public LoginException(Reason reason) {
		this.reason = reason;
	}
	
	public Reason getReason() {
        return this.reason;
    }
}
