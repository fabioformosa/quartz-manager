package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AnonAuthentication extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;

	public AnonAuthentication() {
		super( null );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
    return getClass() == obj.getClass();
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
	public int hashCode() {
		return 7;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}


}
