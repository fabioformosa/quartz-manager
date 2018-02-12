package it.fabioformosa.quartzmanager.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.fabioformosa.quartzmanager.security.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	Authority findByName(String name);
}
