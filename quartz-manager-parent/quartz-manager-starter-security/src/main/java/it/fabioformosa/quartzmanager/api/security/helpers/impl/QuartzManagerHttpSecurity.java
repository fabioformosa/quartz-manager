package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.fabioformosa.quartzmanager.api.security.helpers.LoginConfigurer;

/**
 * It wraps the httpSecurity to provide new function as login and logout
 *
 */
public class QuartzManagerHttpSecurity extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    public static QuartzManagerHttpSecurity from(HttpSecurity httpSecurity){
        QuartzManagerHttpSecurity newInstance = new QuartzManagerHttpSecurity(httpSecurity);
        newInstance.setBuilder(httpSecurity);
        return newInstance;
    }

    private HttpSecurity httpSecurity;

    private LoginConfigurer loginConfigurer;

    private LogoutSuccess logoutSuccess;

    public QuartzManagerHttpSecurity(HttpSecurity httpSecurity) {
        this.httpSecurity = httpSecurity;
    }

    public QuartzManagerHttpSecurity login(String loginPath, AuthenticationManager authenticationManager) throws Exception {
        if(loginConfigurer == null || logoutSuccess == null)
            throw new IllegalStateException("QuartzManagerHttpSecurity requires to be set loginConfigurer and logoutSuccess!");
        httpSecurity = loginConfigurer.login(loginPath, httpSecurity, authenticationManager);
        return this;
    }


    public LogoutConfigurer<HttpSecurity> logout(String logoutPath) throws Exception {
        LogoutConfigurer<HttpSecurity> logoutConfigurer = httpSecurity.logout().logoutRequestMatcher(new AntPathRequestMatcher(logoutPath))
                .logoutSuccessHandler(logoutSuccess);
        String cookie = loginConfigurer.cookieMustBeDeletedAtLogout();
        if(cookie != null)
            logoutConfigurer.deleteCookies(cookie);
        return logoutConfigurer;
    }

    public QuartzManagerHttpSecurity withLoginConfigurer(LoginConfigurer loginConfigurer, LogoutSuccess logoutSuccess) {
        this.loginConfigurer = loginConfigurer;
        this.logoutSuccess = logoutSuccess;
        return this;
    }
}
