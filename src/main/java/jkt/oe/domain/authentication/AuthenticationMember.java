package jkt.oe.domain.authentication;

public class AuthenticationMember {

    private final Long memberId;
    private final String loginId;
    private final String password;
    private final String salt;

    // 계정 활성 여부
    private final boolean enabled;
    // 계정 잠금 여부 (로그인 실패 횟수 초과 등)
    private final boolean locked;
    // 계정 탈퇴 여부
    private final boolean withdrawn;

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