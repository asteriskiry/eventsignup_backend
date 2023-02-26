/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Arrays;
import java.util.Collections;


public class SpringSecurityConfig {

    @Profile("!dev")
    @KeycloakConfiguration
    public static class Authenticated extends KeycloakWebSecurityConfigurerAdapter {

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) {
            KeycloakAuthenticationProvider keycloakAuthenticationProvider =
                keycloakAuthenticationProvider();
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
         * @param http the {@link HttpSecurity} to modify
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/archive/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/archive/event").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.PUT, "/api/archive/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/archive/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/event/create").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/api/event/banner/add").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/event/banner/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/event/edit/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/api/event/remove/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/event/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.yaml").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.json").hasRole("ADMIN");
        }

        protected CorsConfiguration getCorsConfs() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("https://asteriski.fi")); // should read from configs
            configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            return configuration;
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = getCorsConfs();
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }




         @Bean
        public SessionRegistry buildSessionRegistry() {
            return new SessionRegistryImpl();
        }
    }

    @Profile("dev")
    @KeycloakConfiguration
    public static class DevAuths extends Authenticated {
        @Override
        protected CorsConfiguration getCorsConfs() {
            CorsConfiguration configuration = super.getCorsConfs();
            configuration.addAllowedOrigin("http://localhost:3000");
            return configuration;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs.yaml").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs.json").permitAll()
                .and()
                .csrf().disable();

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
