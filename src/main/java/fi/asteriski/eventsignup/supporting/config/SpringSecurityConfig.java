/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.supporting.config;

import static org.springframework.http.HttpHeaders.*;

import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static fi.asteriski.eventsignup.supporting.utils.Constants.*;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Profile({"dev", "special"})
    public static class DevSecurityConfig {
        @Bean
        public SecurityFilterChain configureDev(@NonNull HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(
                    authorizeHttpRequests -> authorizeHttpRequests.anyRequest().permitAll());
            http.cors(Customizer.withDefaults());
            http.csrf(AbstractHttpConfigurer::disable);

            return http.build();
        }
    }

    @Profile({"!dev & !special"})
    public static class ProdSecurityConfig {
        @Value("${spring.security.oauth2.client.provider.external.issuer-uri}")
        private String keycloakUrl;

        @Value("${fi.asteriski.config.security.logout-redirect-url}")
        private String redirectUrl;

        @Bean
        public SecurityFilterChain configureProd(@NonNull HttpSecurity http) throws Exception {
            http.cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable) // Disabled as we're using token-based auth
                    .oauth2Client(Customizer.withDefaults())
                    .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                        httpSecurityOAuth2LoginConfigurer.tokenEndpoint(Customizer.withDefaults());
                        httpSecurityOAuth2LoginConfigurer.userInfoEndpoint(Customizer.withDefaults());
                    })
                    .sessionManagement(sessionManagement ->
                            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                            .requestMatchers("/unauthenticated", "/oauth2/**", "/login/**")
                            .permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**")
                            .permitAll() // Allow CORS preflight requests
                            .requestMatchers(HttpMethod.GET, API_PATH_ADMIN + "/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.POST, API_PATH_ADMIN + "/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.GET, API_PATH_ARCHIVE + "/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.PUT, API_PATH_ARCHIVE + "/event")
                            .hasAnyRole(ROLE_ADMIN, ROLE_USER)
                            .requestMatchers(HttpMethod.PUT, API_PATH_ARCHIVE + "/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.DELETE, API_PATH_ARCHIVE + "/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.POST, API_PATH_EVENT + "/create")
                            .hasAnyRole(ROLE_ADMIN, ROLE_USER)
                            .requestMatchers(HttpMethod.POST, API_PATH_EVENT + "/banner/add")
                            .hasAnyRole(ROLE_ADMIN, ROLE_USER)
                            .requestMatchers(HttpMethod.GET, API_PATH_EVENT + "/banner/**")
                            .permitAll()
                            .requestMatchers(HttpMethod.PUT, API_PATH_EVENT + "/edit/**")
                            .hasAnyRole(ROLE_ADMIN, ROLE_USER)
                            .requestMatchers(HttpMethod.DELETE, API_PATH_EVENT + "/remove/**")
                            .hasAnyRole(ROLE_ADMIN, ROLE_USER)
                            .requestMatchers(HttpMethod.GET, API_PATH_EVENT + "/**")
                            .hasAnyRole(ROLE_ADMIN, ROLE_USER)
                            .requestMatchers(HttpMethod.GET, API_PATH_SIGNUP + "/**")
                            .permitAll()
                            .requestMatchers(HttpMethod.POST, API_PATH_SIGNUP + "/**")
                            .permitAll()
                            .requestMatchers(HttpMethod.DELETE, API_PATH_SIGNUP + "/**")
                            .permitAll()
                            .requestMatchers(HttpMethod.GET, "/swagger-ui/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.GET, "/api-docs/**")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.GET, "/api-docs.yaml")
                            .hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.GET, "/api-docs.json")
                            .hasRole(ROLE_ADMIN))
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Add JWT support
                    .logout(httpSecurityLogoutConfigurer -> {
                        var url = String.format(
                                "%s/protocol/openid-connect/logout?redirect=%s", keycloakUrl, redirectUrl);
                        httpSecurityLogoutConfigurer.logoutSuccessUrl(url);
                    });

            return http.build();
        }
    }

    @Configuration
    public static class CorsConfig {
        @Value("${fi.asteriski.config.security.allowed-cors-domain}")
        private String allowedCorsOrigin;

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            var configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of(allowedCorsOrigin));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of(
                    AUTHORIZATION,
                    CONTENT_TYPE,
                    "X-Requested-With",
                    ACCEPT,
                    ORIGIN,
                    ACCESS_CONTROL_REQUEST_METHOD,
                    ACCESS_CONTROL_REQUEST_HEADERS));
            configuration.setExposedHeaders(List.of(ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ALLOW_CREDENTIALS));
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
    }
}
