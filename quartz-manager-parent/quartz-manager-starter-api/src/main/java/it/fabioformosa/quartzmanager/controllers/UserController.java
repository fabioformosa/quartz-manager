package it.fabioformosa.quartzmanager.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/quartz-manager/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @GetMapping("/whoami")
    public @ResponseBody Object user() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null && context.getAuthentication() != null)
            return context.getAuthentication().getPrincipal();
        return "\"NO_AUTH\"";
    }

//    /**
//     * JWT Temporary disabled
//     *
//     * @author Fabio.Formosa
//     *
//     */

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

}
