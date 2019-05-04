package it.fabioformosa.quartzmanager.security.service;

import java.util.List;

import it.fabioformosa.quartzmanager.security.model.Authority;

public interface AuthorityService {
	List<Authority> findById(Long id);

	List<Authority> findByname(String name);

}
