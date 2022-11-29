package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@EqualsAndHashCode
public class AnonAuthentication extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;

	public AnonAuthentication() {
		super( null );
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}


}
