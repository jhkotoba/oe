package jkt.oe.module.auth.exception;

/**
 * 가입 과정에서 발생할 수 있는 예외를 처리하기 위한 커스텀 런타임 예외 클래스
 */
public class SignupException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Reason reason;
	
	public enum Reason {
		
		/**
         * 전달받은 사용자 ID 정보가 있어서 회원가입을 진행하지 못하는 경우
         * 코드: 20001
         */
		USER_ID_ALREADY_EXISTS("20001", "이미 존재하는 사용자 ID입니다.");
		
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
	
	public SignupException(Reason reason) {
		this.reason = reason;
	}
	
	public Reason getReason() {
        return this.reason;
    }
}
