/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import java.util.List;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

public class SpringSecurityConfig {

    @Profile("!dev & !special")
    @KeycloakConfiguration
    public static class Authenticated extends KeycloakWebSecurityConfigurerAdapter {

        @Value("${fi.asteriski.config.security.allowedCorsDomain}")
        private String allowedCorsOrigin;

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) {
            KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
            keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
            auth.authenticationProvider(keycloakAuthenticationProvider);
        }

        @Bean
        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            return new NullAuthenticatedSessionStrategy();
        }

        /**
         * Configures end point security.
         *
         * @param http the {@link HttpSecurity} to modify
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http.cors()
                    .configurationSource(corsConfigurationSource())
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/admin/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/admin/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/archive/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/archive/event")
                    .hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.PUT, "/api/archive/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/archive/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/event/create")
                    .hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.POST, "/api/event/banner/add")
                    .hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.GET, "/api/event/banner/**")
                    .permitAll()
                    .antMatchers(HttpMethod.PUT, "/api/event/edit/**")
                    .hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.DELETE, "/api/event/remove/**")
                    .hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.GET, "/api/event/**")
                    .hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.GET, "/api/signup/**")
                    .permitAll()
                    .antMatchers(HttpMethod.POST, "/api/signup/**")
                    .permitAll()
                    .antMatchers(HttpMethod.DELETE, "/api/signup/**")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/swagger-ui/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api-docs/**")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api-docs.yaml")
                    .hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api-docs.json")
                    .hasRole("ADMIN");
        }

        protected CorsConfiguration getCorsConfiguration() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of(allowedCorsOrigin));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(true);
            return configuration;
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = getCorsConfiguration();
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

        @Bean
        public SessionRegistry buildSessionRegistry() {
            return new SessionRegistryImpl();
        }
    }

    @Profile({"dev", "special"})
    @KeycloakConfiguration
    public static class DevAuths extends Authenticated {
        @Override
        protected CorsConfiguration getCorsConfiguration() {
            CorsConfiguration configuration = super.getCorsConfiguration();
            configuration.addAllowedOrigin("http://localhost:3000");
            return configuration;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/swagger-ui/**")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api-docs/**")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api-docs.yaml")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api-docs.json")
                    .permitAll()
                    .and()
                    .csrf()
                    .disable();
        }
    }
}

@Configuration
class ApplicationConfiguration {

    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
