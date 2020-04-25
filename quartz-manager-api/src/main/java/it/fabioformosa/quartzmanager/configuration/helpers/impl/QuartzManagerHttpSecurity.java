package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class QuartzManagerHttpSecurity extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private ApplicationContext applicationContext;
  private HttpSecurity httpSecurity;

  private final LoginConfig loginConfig;

  public static QuartzManagerHttpSecurity from(HttpSecurity httpSecurity){
    QuartzManagerHttpSecurity newInstance = new QuartzManagerHttpSecurity(httpSecurity);
    newInstance.setBuilder(httpSecurity);
    return newInstance;
  }

  public QuartzManagerHttpSecurity(HttpSecurity httpSecurity) {
    this.httpSecurity = httpSecurity;
    this.applicationContext = httpSecurity.getSharedObject(ApplicationContext.class);
    this.loginConfig = this.applicationContext.getBean(LoginConfig.class);
  }

  public QuartzManagerHttpSecurity login(AuthenticationManager authenticationManager) throws Exception {
    httpSecurity = loginConfig.login(httpSecurity, authenticationManager);
    return this;
  }


  public LogoutConfigurer<HttpSecurity> logout() throws Exception {
    return httpSecurity.logout();
  }
}
