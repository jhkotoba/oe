package jkt.oe.module.auth.login.exception;

/**
 * 로그인 관련 예외처리 Exception
 */
public class LoginException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Reason reason;
	
	public enum Reason {
		
		USER_NOT_FOUND("10001", "존재하지 않은 사용자 입니다.");
		
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
