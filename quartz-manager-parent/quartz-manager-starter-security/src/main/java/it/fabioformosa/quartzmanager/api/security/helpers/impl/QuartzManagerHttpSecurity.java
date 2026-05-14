package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import it.fabioformosa.quartzmanager.api.security.helpers.LoginConfigurer;

/**
 * It wraps the httpSecurity to provide new function as login and logout
 *
 */
public class QuartzManagerHttpSecurity {

    public static QuartzManagerHttpSecurity from(HttpSecurity httpSecurity){
        QuartzManagerHttpSecurity newInstance = new QuartzManagerHttpSecurity(httpSecurity);
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


    public HttpSecurity logout(String logoutPath) throws Exception {
        String cookie = loginConfigurer.cookieMustBeDeletedAtLogout();
        return httpSecurity.logout(logout -> {
            logout.logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(logoutPath));
            logout.logoutSuccessHandler(logoutSuccess);
            if(cookie != null)
                logout.deleteCookies(cookie);
        });
    }

    public QuartzManagerHttpSecurity withLoginConfigurer(LoginConfigurer loginConfigurer, LogoutSuccess logoutSuccess) {
        this.loginConfigurer = loginConfigurer;
        this.logoutSuccess = logoutSuccess;
        return this;
    }
}
