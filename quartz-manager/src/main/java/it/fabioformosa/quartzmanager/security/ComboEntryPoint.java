package it.fabioformosa.quartzmanager.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class ComboEntryPoint extends LoginUrlAuthenticationEntryPoint {

	private static final String LOGIN_FORM_URL = "/login";

	public ComboEntryPoint() {
		super(LOGIN_FORM_URL);
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		if (RESTRequestMatcher.isRestRequest(request)
				|| WebsocketRequestMatcher.isWebsocketConnectionRequest(request))
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		else
			super.commence(request, response, authException);
	}

}
