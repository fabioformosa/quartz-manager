package it.fabioformosa.quartzmanager.configuration.helpers.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class QuartzManagerHttpSecurity {

  static QuartzManagerHttpSecurity from(HttpSecurity httpSecurity) {
    QuartzManagerHttpSecurity newInstance = new QuartzManagerHttpSecurity();
    newInstance.httpSecurity = httpSecurity;
    return newInstance;
  }

  private HttpSecurity httpSecurity;

  QuartzManagerHttpSecurity login(AuthenticationManager authenticationManager){
    return this;
  }

  QuartzManagerHttpSecurity logout(){
    return this;
  }

}
