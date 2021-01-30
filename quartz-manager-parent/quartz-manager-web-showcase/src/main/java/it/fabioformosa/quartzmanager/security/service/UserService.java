package it.fabioformosa.quartzmanager.security.service;

import java.util.List;

import it.fabioformosa.quartzmanager.security.model.User;
import it.fabioformosa.quartzmanager.security.model.UserRequest;

public interface UserService {
	List<User> findAll();

	User findById(Long id);

	User findByUsername(String username);

	void resetCredentials();

	User save(UserRequest user);
}
