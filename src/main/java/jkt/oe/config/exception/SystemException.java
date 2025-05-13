package jkt.oe.config.exception;

/**
 * 시스템 예외처리 클래스 
 * 시스템 관련 예외를 처리하기 위한 커스텀 런타임 예외
 */
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final Reason reason;
	
	public enum Reason {
		
		/**
         * 알고리즘이 존재하지 않을 때 발생하는 오류
         * 코드: 90101, 메시지: SYSTEM ERROR-90101
         */
		NO_SUCH_ALGORITHM ("90101", "SYSTEM ERROR-90101");
		
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
	
	public SystemException(Reason reason, Throwable cause) {
		super(reason.getMessage(), cause);
		this.reason = reason;
	}
	
	public Reason getReason() {
        return this.reason;
    }
}
