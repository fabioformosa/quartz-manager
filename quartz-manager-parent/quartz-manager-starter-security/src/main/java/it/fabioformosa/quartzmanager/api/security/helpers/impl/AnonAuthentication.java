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
		if ( getClass() != obj.getClass() )
			return false;
		return true;
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
		int hash = 7;
		return hash;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}


}
