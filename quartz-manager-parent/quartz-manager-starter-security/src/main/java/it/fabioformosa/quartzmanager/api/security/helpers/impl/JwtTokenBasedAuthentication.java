package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;


@EqualsAndHashCode(callSuper = true)
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

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  public void setToken( String token ) {
    this.token = token;
  }

}
