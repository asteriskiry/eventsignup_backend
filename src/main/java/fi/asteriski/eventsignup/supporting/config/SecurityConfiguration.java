package fi.asteriski.eventsignup.supporting.config;

import static fi.asteriski.eventsignup.supporting.utils.Constants.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import jakarta.servlet.Filter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!test & !special")
@Log4j2
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(GET, API_PATH_EVENT + "/users-events")
                        .permitAll()
                        .requestMatchers(GET, API_PATH_EVENT + "/events")
                        .hasRole(ROLE_USER)
                        .requestMatchers(POST, API_PATH_EVENT + "/create")
                        .hasRole(ROLE_USER)
                        .requestMatchers(POST, API_PATH_EVENT + "/update")
                        .hasRole(ROLE_USER)
                        .requestMatchers(GET, API_PATH_EVENT + "/banner/{fileName}")
                        .hasRole(ROLE_USER)
                        .requestMatchers(GET, "banner/get/{fileName}")
                        .permitAll()
                        .requestMatchers(POST, API_PATH_EVENT + "/banner/add")
                        .hasRole(ROLE_USER)
                        .requestMatchers(GET, API_PATH_FORM + "/{formId:" + UUID_REGEX + "}")
                        .hasRole(ROLE_USER)
                        .requestMatchers(POST, API_PATH_FORM + "/create")
                        .hasRole(ROLE_USER)
                        .requestMatchers(PUT, API_PATH_FORM + "/update")
                        .hasRole(ROLE_USER)
                        .requestMatchers(GET, API_PATH_SIGNUP + "/{eventId:" + UUID_REGEX + "}")
                        .permitAll()
                        .requestMatchers(GET, API_PATH_SIGNUP + "/{formId:" + UUID_REGEX + "}/participants")
                        .permitAll()
                        .requestMatchers(GET, API_PATH_PARTICIPANT + "/{formId:" + UUID_REGEX + "}/participants/names")
                        .hasRole(ROLE_USER)
                        .requestMatchers(GET, API_PATH_PARTICIPANT + "/{formId:" + UUID_REGEX + "}/participants")
                        .hasRole(ROLE_USER)
                        .requestMatchers(POST, API_PATH_PARTICIPANT + "/{formId:" + UUID_REGEX + "}/signup")
                        .hasRole(ROLE_USER)
                        .requestMatchers(GET, "/swagger-ui/**")
                        .hasRole(ROLE_ADMIN)
                        .requestMatchers(GET, "/api-docs/**")
                        .hasRole(ROLE_ADMIN)
                        .requestMatchers(GET, "/api-docs.yaml")
                        .hasRole(ROLE_ADMIN)
                        .requestMatchers(GET, "/api-docs.json")
                        .hasRole(ROLE_ADMIN))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public Filter debugLoggingFilter() {
        return (request, response, chain) -> {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug(
                        "User authorities: {}",
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
            chain.doFilter(request, response);
        };
    }
}
