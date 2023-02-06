/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import fi.asteriski.eventsignup.filter.JwtFilter;
import fi.asteriski.eventsignup.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SpringSecurityConfig {

    @EnableWebSecurity
    @AllArgsConstructor
    @Profile("!dev")
    public static class Authenticated extends WebSecurityConfigurerAdapter {

        private final UserService userService;

        private final BCryptPasswordEncoder bCryptPasswordEncoder;
        protected JwtFilter jwtFilter;

        /**
         * Configures end point security.
         * @param http the {@link HttpSecurity} to modify
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }))
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/event/create").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/api/event/banner/add").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/event/banner/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/event/edit/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/api/event/remove/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/event/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.yaml").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.json").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/signup/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable().cors().disable(); // Don't disable these in prod
        }

        /**
         * Configures end point security.
         * @param http the {@link HttpSecurity} to modify
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }))
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/event/create").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/api/event/banner/add").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/event/banner/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/event/edit/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/api/event/remove/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/event/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.yaml").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.json").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/signup/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors().configurationSource(corsConfigurationSource());
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


        /**
         * Configures to use custom service for user management and password encrypting method..
         * @param auth Builtin AuthenticationManagerBuilder entity.
         * @throws Exception
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);
        }

        @Override @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @EnableWebSecurity
    @Profile("dev")
    public static class DevAuths extends Authenticated {

        public DevAuths(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtFilter jwtFilter) {
            super(userService, bCryptPasswordEncoder, jwtFilter);
        }

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

