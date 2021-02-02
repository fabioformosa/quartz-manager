package it.fabioformosa.quartzmanager.security.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.fabioformosa.quartzmanager.security.models.User;
import it.fabioformosa.quartzmanager.security.repositories.UserRepository;

/**
 * Temporary disabled
 * @author Fabio
 *
 */
//@Service
public class CustomUserDetailsService implements UserDetailsService {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public void changePassword(String oldPassword, String newPassword) {

        //		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        //		String username = currentUser.getName();
        //
        //		if (authenticationManager != null) {
        //			LOGGER.debug("Re-authenticating user '"+ username + "' for password change request.");
        //
        //			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        //		} else {
        //			LOGGER.debug("No authentication manager set. can't change Password!");
        //
        //			return;
        //		}
        //
        //		LOGGER.debug("Changing password for user '"+ username + "'");
        //
        //		User user = (User) loadUserByUsername(username);
        //
        //		user.setPassword(passwordEncoder.encode(newPassword));
        //		userRepository.save(user);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        else
            return user;
    }

}
