package it.fabioformosa.quartzmanager.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.fabioformosa.quartzmanager.security.TokenHelper;
import it.fabioformosa.quartzmanager.security.model.UserTokenState;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${jwt.expires_in_sec}")
	private int EXPIRES_IN_SEC;

	@Value("${jwt.cookie}")
	private String TOKEN_COOKIE;

	@Autowired
	TokenHelper tokenHelper;
	//
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication ) throws IOException, ServletException {
		clearAuthenticationAttributes(request);
		User user = (User)authentication.getPrincipal();

		String jws = tokenHelper.generateToken( user.getUsername() );

		Cookie authCookie = new Cookie( TOKEN_COOKIE, jws );

		authCookie.setHttpOnly( true );

		authCookie.setMaxAge( EXPIRES_IN_SEC );

		authCookie.setPath( "/quartz-manager" );
		response.addCookie( authCookie );

		// JWT is also in the response
		UserTokenState userTokenState = new UserTokenState(jws, EXPIRES_IN_SEC);
		String jwtResponse = objectMapper.writeValueAsString( userTokenState );
		response.setContentType("application/json");
		response.getWriter().write( jwtResponse );

	}

	//		@Override
	//		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	//				Authentication authentication ) throws IOException, ServletException {
	//			//		clearAuthenticationAttributes(request);
	//			response.setContentType("application/json");
	//			response.getWriter().write( objectMapper.writeValueAsString("OK"));
	//		}
}
