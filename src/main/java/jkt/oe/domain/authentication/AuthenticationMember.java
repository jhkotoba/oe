package jkt.oe.domain.authentication;

/**
 * 인증용 회원 정보 도메인 객체
 */
public class AuthenticationMember {

    // 회원 식별자
    private final Long memberId;

    // 로그인 아이디
    private final String loginId;

    // 암호화된 비밀번호
    private final String password;

    // 비밀번호 솔트
    private final String salt;

    // 계정 활성 여부
    private final boolean enabled;

    // 계정 잠금 여부 (로그인 실패 횟수 초과 등)
    private final boolean locked;
    
    // 계정 탈퇴 여부
    private final boolean withdrawn;

    /**
     * 인증용 회원 정보 생성자
     * @param memberId 회원 식별자
     * @param loginId 로그인 아이디
     * @param password 암호화된 비밀번호
     * @param salt 비밀번호 솔트
     * @param enabled 계정 활성 여부
     * @param locked 계정 잠금 여부 (로그인 실패 횟수 초과 등)
     * @param withdrawn 계정 탈퇴 여부
     */
    public AuthenticationMember(Long memberId, String loginId, String password, String salt,
                                boolean enabled, boolean locked, boolean withdrawn) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.salt = salt;
        this.enabled = enabled;
        this.locked = locked;
        this.withdrawn = withdrawn;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }
}