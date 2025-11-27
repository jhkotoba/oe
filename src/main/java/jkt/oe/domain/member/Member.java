package jkt.oe.domain.member;

import java.time.LocalDateTime;

public class Member {

    /**
     * 회원 식별자 (PK)
     * - 신규 회원가입 시점에는 null 일 수 있다.
     */
    private final Long memberId;

    /**
     * 로그인 아이디
     */
    private final String loginId;

    /**
     * 이메일
     */
    private final String email;

    /**
     * 해시된 비밀번호
     */
    private final String password;

    /**
     * 비밀번호 해시 생성에 사용된 솔트
     */
    private final String salt;

    /**
     * 계정 활성 여부 (IS_ACTIVE)
     */
    private final boolean active;

    /**
     * 생성 시각 (CREATED_AT)
     */
    private final LocalDateTime createdAt;

    /**
     * 수정 시각 (UPDATED_AT)
     */
    private final LocalDateTime updatedAt;

    /**
     * 회원 도메인 객체 생성자
     *
     * @param memberId  회원 식별자 (신규 가입 시 null 가능)
     * @param loginId   로그인 아이디
     * @param email     이메일
     * @param password  해시된 비밀번호
     * @param salt      비밀번호 솔트
     * @param active    계정 활성 여부
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    private Member(Long memberId,
            String loginId,
            String email,
            String password,
            String salt,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 회원가입을 위한 정적 팩토리 메소드
     * <p>
     * - memberId, createdAt, updatedAt 은 인프라 계층에서 채운다.
     *
     * @param loginId  로그인 아이디
     * @param email    이메일
     * @param password 해시된 비밀번호
     * @param salt     비밀번호 솔트
     * @return 신규 회원 도메인 객체
     */
    public static Member createForSignup(String loginId,
            String email,
            String password,
            String salt) {
        return new Member(
                null, // 아직 DB에 저장 전이므로 null
                loginId,
                email,
                password,
                salt,
                true, // 가입 시 기본 활성
                null, // 인프라에서 채움
                null // 인프라에서 채움
        );
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 계정 비활성화 도메인 메소드 예시
     *
     * @return 비활성화된 새 Member 객체
     */
    public Member deactivate() {
        return new Member(
                this.memberId,
                this.loginId,
                this.email,
                this.password,
                this.salt,
                false,
                this.createdAt,
                this.updatedAt);
    }
}