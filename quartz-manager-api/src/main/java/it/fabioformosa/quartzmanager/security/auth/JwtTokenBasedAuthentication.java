package it.fabioformosa.quartzmanager.security.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;


public class JwtTokenBasedAuthentication extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 1L;

  private String token;
  private final UserDetails principle;

  public JwtTokenBasedAuthentication(UserDetails principle) {
    super(principle.getAuthorities());
    this.principle = principle;
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public UserDetails getPrincipal() {
    return principle;
  }

  public String getToken() {
    return token;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  public void setToken( String token ) {
    this.token = token;
  }

}
