/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Value("${default.days.until.password.expire}")
    private Integer passwordExpiryDays;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException(String.format("User '%s' not found.", username)));
    }

    void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setExpirationDate(Instant.now().plus(passwordExpiryDays, ChronoUnit.DAYS));
        user.setEnabled(true);
        if (user.isAdmin()) {
            user.setUserRole(UserRole.ROLE_ADMIN);
        }

        userRepository.save(user);
    }

    List<User> getAllNonAdminUsers() {
        List<User> users = userRepository.findAll();
        users.removeIf(user -> UserRole.ROLE_ADMIN.equals(user.getUserRole()));
        users.forEach(user -> user.setPassword(""));
        return users;
    }

    void editUser(User user) {
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
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

    void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    void deleteAllUsers() {
        userRepository.deleteAllByUserRole(UserRole.ROLE_USER);
    }
}
