package it.fabioformosa.quartzmanager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/session")
public class SessionController {

	private final Logger log = LoggerFactory.getLogger(SessionController.class);

	@GetMapping("/invalidate")
	//@PreAuthorize("hasAuthority('ADMIN')") TODO
	@ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(hidden = true)
	public void invalidateSession(HttpSession session) {
		session.invalidate();
		log.info("Invalidated current session!");
	}

	@GetMapping("/refresh")
//	@PreAuthorize("hasAuthority('ADMIN')") TODO
  @Operation(hidden = true)
	public HttpEntity<Void> refreshSession(HttpSession session) {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
