package it.fabioformosa.quartzmanager.api.security.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenState {
	private String accessToken;
	private Long expiresInSec;

	public UserTokenState(String accessToken, long expiresInSec) {
		this.accessToken = accessToken;
		this.expiresInSec = expiresInSec;
	}

}
