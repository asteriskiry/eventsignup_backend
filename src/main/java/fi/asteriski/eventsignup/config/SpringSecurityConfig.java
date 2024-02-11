/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import static fi.asteriski.eventsignup.utils.Constants.*;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Profile({"dev", "special"})
    public static class DevSecurityConfig {
        @Bean
        public SecurityFilterChain configureDev(@NonNull HttpSecurity http) throws Exception {
            http.cors(Customizer.withDefaults()).csrf(Customizer.withDefaults());

            return http.build();
        }
    }

    @Profile({"!dev & !special"})
    public static class ProdSecurityConfig {
        @Value("${spring.security.oauth2.client.provider.external.issuer-uri}")
        private String keycloakUrl;

        @Value("${fi.asteriski.config.security.logout-redirect-url}")
        private String redirectUrl;

        @Value("${fi.asteriski.config.security.allowed-cors-domain}")
        private String allowedCorsOrigin;

        @Bean
        public SecurityFilterChain configureProd(@NonNull HttpSecurity http) throws Exception {
            http.oauth2Client(Customizer.withDefaults())
                    .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                        httpSecurityOAuth2LoginConfigurer.tokenEndpoint(Customizer.withDefaults());
                        httpSecurityOAuth2LoginConfigurer.userInfoEndpoint(Customizer.withDefaults());
                    })
                    .sessionManagement(
                            sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                    .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                            .requestMatchers("/unauthenticated", "/oauth2/**", "/login/**")
                            .permitAll()
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
                    .logout(httpSecurityLogoutConfigurer -> {
                        var url = String.format(
                                "%s/protocol/openid-connect/logout?redirect=%s", keycloakUrl, redirectUrl);
                        httpSecurityLogoutConfigurer.logoutSuccessUrl(url);
                    });

            return http.build();
        }

        private CorsConfiguration getCorsConfiguration() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of(allowedCorsOrigin));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(true);
            return configuration;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = getCorsConfiguration();
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
    }
}
