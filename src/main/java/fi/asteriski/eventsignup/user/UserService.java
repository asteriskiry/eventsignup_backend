/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private static final String LOG_PREFIX = "[UserService]";
    @Value("${default.days.until.password.expire}")
    private static Integer passwordExpiryDays;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Fetches a user from database on login. Called automatically by Spring.
     *
     * @param username the username identifying the user whose data is required.
     * @return The requested user.
     * @throws UsernameNotFoundException If user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException(String.format("User '%s' not found.", username)));
    }

    /**
     * Adds a new user to the database.
     *
     * @param user User entity to save.
     */
    void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setExpirationDate(Instant.now().plus(passwordExpiryDays, ChronoUnit.DAYS));
        user.setEnabled(true);
        if (user.isAdmin()) {
            user.setUserRole(UserRole.ROLE_ADMIN);
        }
        log.info(String.format("%s Adding new user: '%s'", LOG_PREFIX, user));
        userRepository.save(user);
    }

    /**
     * <p>Fetches all non-admin users from database.<br>
     * Sets all passwords to an empty string for security reasons.</p>
     *
     * @return List of users.
     */
    List<User> getAllNonAdminUsers() {
        List<User> users = userRepository.findAll();
        users.removeIf(user -> UserRole.ROLE_ADMIN.equals(user.getUserRole()));
        users.forEach(user -> user.setPassword(""));
        return users;
    }

    /**
     * Edits an existing user and saves it to database.
     *
     * @param user New user entity to apply changes from to existing user.
     */
    void editUser(User user) {
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(() -> {
            log.error(String.format("%s Unable to edit user. Existing user with id <%s> not found.", LOG_PREFIX, user.getId()));
            throw new UserNotFoundException();
        });
        userFromDb.setFirstName(userFromDb.getFirstName());
        userFromDb.setLastName(userFromDb.getLastName());
        userFromDb.setEmail(user.getEmail());
        userFromDb.setUsername(userFromDb.getUsername());
        userFromDb.setUserRole(userFromDb.getUserRole());
        userFromDb.setEnabled(user.getEnabled());
        if (!"".equals(user.getPassword())) {
            userFromDb.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userFromDb.setExpirationDate(Instant.now().plus(passwordExpiryDays, ChronoUnit.DAYS));
        }
        if (user.isAdmin()) {
            userFromDb.setUserRole(UserRole.ROLE_ADMIN);
        }
        userRepository.save(userFromDb);
    }

    /**
     * Deletes a user from database.
     *
     * @param userId Id of the user to delete.
     */
    void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Deletes of non-admin users from database.
     */
    void deleteAllNonAdminUsers() {
        userRepository.deleteAllByUserRole(UserRole.ROLE_USER);
    }
}
