package it.fabioformosa.quartzmanager.security.repository;

import it.fabioformosa.quartzmanager.security.model.User;

public interface UserRepository {
	User findByUsername( String username );
}
//public interface UserRepository extends JpaRepository<User, Long> {
//	User findByUsername( String username );
//}

