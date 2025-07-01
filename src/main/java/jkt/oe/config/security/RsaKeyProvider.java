package jkt.oe.config.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * RSA 키를 로딩, 전역에서 재사용할 수 있도록 관리 컴포넌트
 */
@Component
public class RsaKeyProvider {
	
	/**
	 * RSA 개인키
	 */
	private final PrivateKey privateKey;
	
	/**
	 * RSA 공개키
	 */
	private final PublicKey publicKey;
	
	/**
	 * 생성자에서 private.pem 키 파일을 한 번 읽고, 파싱하여 privateKey 필드에 저장
	 * @param privateKeyPath
	 * @throws Exception
	 */
    public RsaKeyProvider(
    		@Value("${custom.jwt.private-key-path}") Resource privateKeyPath,
    		@Value("${custom.jwt.public-key-path}") Resource publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	
    	// 파일 내용을 문자열로 읽기
        String key;
        try (InputStream is = privateKeyPath.getInputStream()) {
            key = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        
        // PEM 헤더/푸터 제거 + 공백 제거 → Base64 디코딩 준비
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
        		.replace("-----END PRIVATE KEY-----", "")
        		.replaceAll("\\s", "");

        // Base64 디코딩
        byte[] decoded = Base64.getDecoder().decode(key);
        
        // PKCS#8 형식의 키 사양 객체 생성
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        
        // RSA 알고리즘으로 개인키 생성
        this.privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        
        String pubPem;
        try (InputStream is = publicKeyPath.getInputStream()) {
            pubPem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        pubPem = pubPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] pubDecoded = Base64.getDecoder().decode(pubPem);
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubDecoded);
        this.publicKey = KeyFactory.getInstance("RSA").generatePublic(pubKeySpec);
    }
    
    /**
     * RSA 개인 키 반환
     * @return
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    /**
     * RSA 공개 키 반환
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}
