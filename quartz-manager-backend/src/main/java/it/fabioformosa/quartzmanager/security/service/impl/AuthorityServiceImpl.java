package it.fabioformosa.quartzmanager.security.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.fabioformosa.quartzmanager.security.model.Authority;
import it.fabioformosa.quartzmanager.security.repository.AuthorityRepository;
import it.fabioformosa.quartzmanager.security.service.AuthorityService;

//@Service
public class AuthorityServiceImpl implements AuthorityService {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Override
	public List<Authority> findById(Long id) {
		Authority auth = this.authorityRepository.getOne(id);
		List<Authority> auths = new ArrayList<>();
		auths.add(auth);
		return auths;
	}

	@Override
	public List<Authority> findByname(String name) {
		Authority auth = this.authorityRepository.findByName(name);
		List<Authority> auths = new ArrayList<>();
		auths.add(auth);
		return auths;
	}

}
