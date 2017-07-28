package it.fabioformosa.quartzmanager.security;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RESTRequestMatcher {

	static private final Logger log = LoggerFactory.getLogger(RESTRequestMatcher.class);

	static public RequestMatcher matcherRequestedWith = new ELRequestMatcher(
			"hasHeader('X-Requested-With','XMLHttpRequest')");
	static public RequestMatcher matcherAccept = new ELRequestMatcher(
			"hasHeader('accept','application/json, text/plain, */*')");

	static public boolean isRestRequest(HttpServletRequest request) {
		log.trace("Detecting if it's an AJAX Request: " + request.getRequestURL() + " accept: "
				+ request.getHeader("accept") + " " + " X-Requested-With: "
				+ request.getHeader("X-Requested-With"));
		return matcherRequestedWith.matches(request) || matcherAccept.matches(request);
	}

}