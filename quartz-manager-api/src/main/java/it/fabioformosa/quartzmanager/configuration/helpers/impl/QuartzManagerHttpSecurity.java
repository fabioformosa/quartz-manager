package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfigurer;
import it.fabioformosa.quartzmanager.security.auth.LogoutSuccess;

public class QuartzManagerHttpSecurity extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  public static QuartzManagerHttpSecurity from(HttpSecurity httpSecurity){
    QuartzManagerHttpSecurity newInstance = new QuartzManagerHttpSecurity(httpSecurity);
    newInstance.setBuilder(httpSecurity);
    return newInstance;
  }

  private HttpSecurity httpSecurity;

  private LoginConfigurer loginConfiger;

  private LogoutSuccess logoutSuccess;

  public QuartzManagerHttpSecurity(HttpSecurity httpSecurity) {
    this.httpSecurity = httpSecurity;
    //    applicationContext = httpSecurity.getSharedObject(ApplicationContext.class);
    //    loginConfiger = applicationContext.getBean(LoginConfigurer.class);
    //    logoutSuccess = applicationContext.getBean(LogoutSuccess.class);
  }

  public QuartzManagerHttpSecurity login(String loginPath, AuthenticationManager authenticationManager) throws Exception {
    httpSecurity = loginConfiger.login(loginPath, httpSecurity, authenticationManager);
    return this;
  }


  public QuartzManagerHttpSecurity loginConfig(LoginConfigurer loginConfigurer, LogoutSuccess logoutSuccess) {
    loginConfiger = loginConfigurer;
    this.logoutSuccess = logoutSuccess;
    return this;
  }

  public LogoutConfigurer<HttpSecurity> logout(String logoutPath) throws Exception {
    LogoutConfigurer<HttpSecurity> logoutConfigurer = httpSecurity.logout().logoutRequestMatcher(new AntPathRequestMatcher(logoutPath))
        .logoutSuccessHandler(logoutSuccess);
    String cookie = loginConfiger.cookieMustBeDeletedAtLogout();
    if(cookie != null)
      logoutConfigurer.deleteCookies(cookie);
    return logoutConfigurer;
  }
}
