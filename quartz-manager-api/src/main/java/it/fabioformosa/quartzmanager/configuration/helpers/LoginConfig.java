package it.fabioformosa.quartzmanager.configuration.helpers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface LoginConfig {

  HttpSecurity configureLoginHandler(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception;

}
