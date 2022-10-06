package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import it.fabioformosa.quartzmanager.api.security.models.UserTokenState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * It depends on @JwtTokenHelper to generate the jwtToken.
 * On login success, it generates the jwtToken and it returns it to the login according to possible strategies: cookie, response header.
 * You can choose the strategy through @JwtSecurityProperties
 *
 */
public class JwtAuthenticationSuccessHandlerImpl implements JwtAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationSuccessHandlerImpl.class);

    private final JwtSecurityProperties jwtSecurityProps;

    private final JwtTokenHelper jwtTokenHelper;

    private final ObjectMapper objectMapper;

    private final String contextPath;

    @Autowired
    public JwtAuthenticationSuccessHandlerImpl(String contextPath, JwtSecurityProperties jwtSecurityProps, JwtTokenHelper jwtTokenHelper, ObjectMapper objectMapper) {
        this.contextPath = contextPath;
        this.jwtSecurityProps = jwtSecurityProps;
        this.jwtTokenHelper = jwtTokenHelper;
        this.objectMapper = objectMapper;
    }

    @Override
    public String cookieMustBeDeletedAtLogout() {
        if(!jwtSecurityProps.getCookieStrategy().isEnabled())
            return null;
        return jwtSecurityProps.getCookieStrategy().getCookie();
    }

    @Override
    public void onLoginSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
        log.debug("Login succeeded, generating jwtToken...");

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtTokenHelper.generateToken(user.getUsername());

        if(jwtSecurityProps.getCookieStrategy().isEnabled()) {
            Cookie authCookie = new Cookie(jwtSecurityProps.getCookieStrategy().getCookie(), jwtToken);
            authCookie.setHttpOnly(true);
            authCookie.setMaxAge((int) jwtSecurityProps.getExpirationInSec());
            authCookie.setPath(contextPath);
            response.addCookie(authCookie);
            log.debug("Set jwtToken into the cookie {}", jwtSecurityProps.getCookieStrategy().getCookie());
        }

        if(jwtSecurityProps.getHeaderStrategy().isEnabled()) {
            jwtTokenHelper.setHeader(response, jwtToken);
            log.debug("Set jwtToken into the response header {}", jwtSecurityProps.getHeaderStrategy().getHeader());
        }

        UserTokenState userTokenState = new UserTokenState(jwtToken, jwtSecurityProps.getExpirationInSec());
        String jwtResponse = objectMapper.writeValueAsString(userTokenState);
        response.setContentType("application/json");
        response.getWriter().write(jwtResponse);
    }
}
