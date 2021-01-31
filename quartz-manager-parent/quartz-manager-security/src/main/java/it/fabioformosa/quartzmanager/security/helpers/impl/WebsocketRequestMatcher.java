package it.fabioformosa.quartzmanager.security.helpers.impl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketRequestMatcher {

	static private final Logger log = LoggerFactory.getLogger(WebsocketRequestMatcher.class);

	static public boolean isWebsocketConnectionRequest(HttpServletRequest request) {
		log.trace("Detecting if it's a Websocket Connection Request: " + request.getRequestURL());
		return request.getServletPath().equals("/progress/info")
				|| request.getServletPath().equals("/logs/info");
	}

}