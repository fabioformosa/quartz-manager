package it.fabioformosa.quartzmanager.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.fabioformosa.quartzmanager.security.JwtTokenHelper;
import it.fabioformosa.quartzmanager.security.model.UserTokenState;
import it.fabioformosa.quartzmanager.security.service.impl.CustomUserDetailsService;

/**
 * JWT Temporary disabled
 *
 * @author Fabio.Formosa
 *
 */

//@RestController
//@RequestMapping( value = "/api", produces = MediaType.APPLICATION_JSON_VALUE )
public class AuthenticationController {

	static class PasswordChanger {
		public String oldPassword;
		public String newPassword;
	}

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	JwtTokenHelper tokenHelper;

	@Value("${quartz-manager.security.jwt.expiration-in-sec}")
	private int EXPIRES_IN_SEC;

	@Value("${quartz-manager.security.jwt.cookie-strategy-cookie}")
	private String TOKEN_COOKIE;

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
		userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);
		Map<String, String> result = new HashMap<>();
		result.put( "result", "success" );
		return ResponseEntity.accepted().body(result);
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request, HttpServletResponse response) {

		String authToken = tokenHelper.getToken( request );
		if (authToken != null && tokenHelper.canTokenBeRefreshed(authToken)) {
			// TODO check user password last update
			String refreshedToken = tokenHelper.refreshToken(authToken);

			Cookie authCookie = new Cookie( TOKEN_COOKIE, refreshedToken );
			authCookie.setPath( "/quartz-manager" );
			authCookie.setHttpOnly( true );
			authCookie.setMaxAge( EXPIRES_IN_SEC );
			// Add cookie to response
			response.addCookie( authCookie );

			UserTokenState userTokenState = new UserTokenState(refreshedToken, EXPIRES_IN_SEC);
			return ResponseEntity.ok(userTokenState);
		} else {
			UserTokenState userTokenState = new UserTokenState();
			return ResponseEntity.accepted().body(userTokenState);
		}
	}

}
