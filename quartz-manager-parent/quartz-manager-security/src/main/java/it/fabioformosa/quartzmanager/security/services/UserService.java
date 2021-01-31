package it.fabioformosa.quartzmanager.security.services;

import java.util.List;

import it.fabioformosa.quartzmanager.security.models.User;
import it.fabioformosa.quartzmanager.security.models.UserRequest;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User findByUsername(String username);

    void resetCredentials();

    User save(UserRequest user);
}
