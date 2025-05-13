package jkt.oe.config.security;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/////////////// 테스트중 /////////////////
@Deprecated
public class JwtUtil {
		
	private static String SECRET_KEY = "test_secret_key";
	
	// 토큰 생성 메서드
	@Deprecated
    public static String generateToken(String username, List<String> roles, long expirationTime) {
    	
    	SecretKey secretKey = Keys.hmacShaKeyFor(JwtUtil.SECRET_KEY.getBytes());
    	
    	return Jwts.builder()
    		.claim("userId", 12345)
    		.claim("userNm", 12345)    		
    		.expiration(new Date(System.currentTimeMillis() + 1000000L))
    		.signWith(secretKey)
    		.compact();
    	
    }	
    
    // 토큰 검증 메서드 (유효하면 Claims 리턴)
	@Deprecated
    public static Claims validateToken(String token) throws JwtException {
    	
    	SecretKey secretKey = Keys.hmacShaKeyFor(JwtUtil.SECRET_KEY.getBytes());
    	
    	return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    	
    	
    }
}
