package jkt.oe.module.auth.exception;

import io.jsonwebtoken.JwtException;


public class TokenException extends JwtException {
	
	private static final long serialVersionUID = 1L;
	
	private final Reason reason;
	
	public enum Reason {		
		
		EXPIRED("10201", "토큰이 만료되었습니다."),
        MALFORMED("10202", "토큰 형식이 올바르지 않습니다."),
        SIGNATURE_INVALID("10203", "토큰 서명 검증에 실패했습니다."),
        UNSUPPORTED("10204", "지원하지 않는 토큰 형식입니다."),
        ILLEGAL_ARGUMENT("10205", "토큰이 비어 있거나 잘못된 인수입니다."),
        PREMATURE("10206", "토큰 사용 가능 시점이 아직 되지 않았습니다."),
        MISSING_CLAIM("10207", "필수 클레임이 누락되었습니다."),
        INCORRECT_CLAIM("10208", "클레임 값이 예상과 일치하지 않습니다."),
        WEAK_KEY("10209", "토큰 서명에 사용된 키가 너무 약합니다."),
        DECODING_ERROR("10210", "토큰 디코딩 중 오류가 발생했습니다."),
		
        REFRESH_RUNTIME_ERROR("10299", "리플레시 토큰 발생중 오류가 발생했습니다.");
		
		
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
	
	public TokenException(Reason reason) {
		super(reason.message);
		this.reason = reason;
	}
	
	public TokenException(Reason reason, Throwable cause) {
        super(reason.getMessage(), cause);
        this.reason = reason;
    }
	
	public Reason getReason() {
        return this.reason;
    }
}
