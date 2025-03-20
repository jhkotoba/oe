package jkt.oe.module.auth.token.refresh.model.response;

public class RefreshResponse {
	private String token;

    public RefreshResponse(String token) {
        this.token = token;
    }

    // getter, setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
