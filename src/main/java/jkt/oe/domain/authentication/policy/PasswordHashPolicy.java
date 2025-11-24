package jkt.oe.domain.authentication.policy;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashPolicy {

    /**
     * 사용자가 입력한 원문 비밀번호와 저장된 해시가 일치하는지 검증
     * 
     * @param rawPassword - 사용자가 입력한 원문 비밀번호
     * @param salt        - 비밀번호 해시에 사용된 솔트
     * @param storedHash  - 데이터베이스에 저장된 비밀번호 해시
     * @return 일치 여부
     */
    public boolean matches(String rawPassword, String salt, String storedHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            md.update(rawPassword.getBytes(StandardCharsets.UTF_8));
            String encoded = String.format("%0128x", new BigInteger(1, md.digest()));

            // 타이밍 공격 방지를 위한 상수 시간 비교
            byte[] actual = storedHash.getBytes(StandardCharsets.UTF_8);
            byte[] expected = encoded.getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(actual, expected);

        } catch (NoSuchAlgorithmException e) {
            // SHA-512 미지원 환경은 시스템 오류로 간주
            throw new IllegalStateException("SHA-512 algorithm not available", e);
        }
    }
}
