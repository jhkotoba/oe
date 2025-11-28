package jkt.oe.domain.member;

public class PasswordHash {

    private final String encoded;

    public PasswordHash(String encoded) {
        this.encoded = encoded;
    }

    public String getEncoded() {
        return encoded;
    }

    // private final String hashedPassword;
    // private final String salt;

    // private PasswordHash(String hashedPassword, String salt) {
    // this.hashedPassword = hashedPassword;
    // this.salt = salt;
    // }

    // public static PasswordHash fromRaw(String rawPassword) {
    //     try {
    //         SecureRandom sr = new SecureRandom();
    //         byte[] saltBytes = new byte[16];
    //         sr.nextBytes(saltBytes);
    //         String salt = Base64.getEncoder().encodeToString(saltBytes);

    //         MessageDigest md = MessageDigest.getInstance("SHA-512");
    //         md.update(salt.getBytes(StandardCharsets.UTF_8));
    //         md.update(rawPassword.getBytes(StandardCharsets.UTF_8));
    //         String hashed = String.format("%0128x", new BigInteger(1, md.digest()));

    //         return new PasswordHash(hashed, salt);
    //     } catch (NoSuchAlgorithmException e) {
    //         throw new SystemException(SystemException.Reason.NO_SUCH_ALGORITHM, e);
    //     }
    // }

    // public boolean matches(String rawPassword) {
    //     try {
    //         MessageDigest md = MessageDigest.getInstance("SHA-512");
    //         md.update(salt.getBytes(StandardCharsets.UTF_8));
    //         md.update(rawPassword.getBytes(StandardCharsets.UTF_8));
    //         String hashed = String.format("%0128x", new BigInteger(1, md.digest()));
    //         return this.hashedPassword.equals(hashed);
    //     } catch (NoSuchAlgorithmException e) {
    //         throw new SystemException(SystemException.Reason.NO_SUCH_ALGORITHM, e);
    //     }
    // }

}
