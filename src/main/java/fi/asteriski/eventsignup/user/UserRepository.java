/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@SuppressWarnings("unused")
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteAllByUserRole(UserRole userRole);
}
