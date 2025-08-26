package fi.asteriski.eventsignup.supporting.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test | special")
public class TestSecurityConfiguration {
    @Bean
    public SecurityFilterChain configureTestAndSpecial(@NonNull HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests.anyRequest().permitAll());
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
