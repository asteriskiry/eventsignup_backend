/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import fi.asteriski.eventsignup.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@AllArgsConstructor
@EnableAsync
public class EventsignupApplication implements CommandLineRunner {

    UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(EventsignupApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.getClass().getSimpleName().contains("Mockito")) {
            var adminUsername = System.getenv("ADMIN_USERNAME");
            var adminPassword = System.getenv("ADMIN_PASSWORD");
            User admin = new User("Asteriski", "Admin", "www-asteriski@utu.fi", bCryptPasswordEncoder.encode(adminPassword), adminUsername);
            admin.setUserRole(UserRole.ROLE_ADMIN);
            if (!userRepository.existsByUsername(adminUsername)) {
                userRepository.save(admin);
            }
        }
    }
}
