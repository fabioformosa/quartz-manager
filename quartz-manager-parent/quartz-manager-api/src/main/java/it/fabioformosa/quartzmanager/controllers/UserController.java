package it.fabioformosa.quartzmanager.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	/**
	 * JWT Temporary disabled
	 *
	 * @author Fabio.Formosa
	 *
	 */

	//	@Autowired
	//	private UserService userService;


	//	@RequestMapping(method = POST, value = "/signup")
	//	public ResponseEntity<?> addUser(@RequestBody UserRequest userRequest,
	//			UriComponentsBuilder ucBuilder) {
	//
	//		User existUser = this.userService.findByUsername(userRequest.getUsername());
	//		if (existUser != null)
	//			throw new ResourceConflictException(userRequest.getId(), "Username already exists");
	//		User user = this.userService.save(userRequest);
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.setLocation(ucBuilder.path("/api/user/{userId}").buildAndExpand(user.getId()).toUri());
	//		return new ResponseEntity<>(user, HttpStatus.CREATED);
	//	}
	//
	//	@RequestMapping(method = GET, value = "/user/all")
	//	public List<User> loadAll() {
	//		return this.userService.findAll();
	//	}
	//
	//	@RequestMapping(method = GET, value = "/user/{userId}")
	//	public User loadById(@PathVariable Long userId) {
	//		return this.userService.findById(userId);
	//	}
	//
	//
	//	@RequestMapping(method = GET, value = "/user/reset-credentials")
	//	public ResponseEntity<Map> resetCredentials() {
	//		this.userService.resetCredentials();
	//		Map<String, String> result = new HashMap<>();
	//		result.put("result", "success");
	//		return ResponseEntity.accepted().body(result);
	//	}

	/*
	 * We are not using userService.findByUsername here(we could), so it is good that we are making
	 * sure that the user has role "ROLE_USER" to access this endpoint.
	 */
	//	@RequestMapping("/whoami")
	//	//	@PreAuthorize("hasRole('USER')")
	//	public User user() {
	//		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	//	}

	@GetMapping("/whoami")
	@PreAuthorize("isAuthenticated()")
	public Object user() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
