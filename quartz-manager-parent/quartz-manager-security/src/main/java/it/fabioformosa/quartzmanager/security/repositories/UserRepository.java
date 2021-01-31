package it.fabioformosa.quartzmanager.security.repositories;

import it.fabioformosa.quartzmanager.security.models.User;

public interface UserRepository {
    User findByUsername( String username );
}
//public interface UserRepository extends JpaRepository<User, Long> {
//	User findByUsername( String username );
//}

