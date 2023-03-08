/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import fi.asteriski.eventsignup.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableMongoAuditing
@RequiredArgsConstructor
public class EventsignupApplication implements CommandLineRunner {

    @NonNull
    UserRepository userRepository;
    @NonNull

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${spring.profiles.active}")
    private String activeSpringProfile;

    public static void main(String[] args) {
        SpringApplication.run(EventsignupApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.getClass().getSimpleName().contains("Mockito") && !activeSpringProfile.equals("special")) {
            var adminUsername = System.getenv("ADMIN_USERNAME");
            var adminPassword = System.getenv("ADMIN_PASSWORD");
            var adminEmail = System.getenv("ADMIN_EMAIL");
            var adminFirstName = System.getenv("ADMIN_FIRST_NAME");
            var adminLastName = System.getenv("ADMIN_LAST_NAME");
            var admin = new User(adminFirstName, adminLastName, adminEmail, bCryptPasswordEncoder.encode(adminPassword), adminUsername);
            admin.setUserRole(UserRole.ROLE_ADMIN);
            if (!userRepository.existsByUsername(adminUsername)) {
                userRepository.save(admin);
            }
        }
    }
}
