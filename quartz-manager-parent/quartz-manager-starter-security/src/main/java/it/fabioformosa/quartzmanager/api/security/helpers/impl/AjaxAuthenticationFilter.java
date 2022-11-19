package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AjaxAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public class AjaxLoginAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
			implements AuthenticationSuccessHandler {

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication)  {
			response.setStatus(HttpServletResponse.SC_OK);
			super.clearAuthenticationAttributes(request);
		}

	}

	public AjaxAuthenticationFilter(AuthenticationManager authenticationManager) {
		setAuthenticationManager(authenticationManager);
		setUsernameParameter("ajaxUsername");
		setPasswordParameter("ajaxPassword");
		setPostOnly(true);
		setFilterProcessesUrl("/ajaxLogin");

		setAuthenticationSuccessHandler(new AjaxLoginAuthSuccessHandler());
	}

	/**
	 * Removes temporary authentication-related data which may have been stored
	 * in the session during the authentication process.
	 */
	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null)
			return;

		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

}
