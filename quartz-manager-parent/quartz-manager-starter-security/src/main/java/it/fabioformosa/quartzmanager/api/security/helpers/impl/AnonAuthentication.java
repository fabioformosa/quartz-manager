package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

@EqualsAndHashCode(callSuper = false)
public class AnonAuthentication extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;

	public AnonAuthentication() {
		super(Collections.emptyList());
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
