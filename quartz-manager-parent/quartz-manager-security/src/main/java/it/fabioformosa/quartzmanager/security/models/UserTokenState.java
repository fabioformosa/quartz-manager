package it.fabioformosa.quartzmanager.security.models;

public class UserTokenState {
	private String access_token;
	private Long expires_in_sec;

	public UserTokenState() {
		this.access_token = null;
		this.expires_in_sec = null;
	}

	public UserTokenState(String access_token, long expires_in_sec) {
		this.access_token = access_token;
		this.expires_in_sec = expires_in_sec;
	}

	public String getAccess_token() {
		return access_token;
	}

	public Long getExpires_in_sec() {
		return expires_in_sec;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public void setExpires_in_sec(Long expires_in_sec) {
		this.expires_in_sec = expires_in_sec;
	}
}
