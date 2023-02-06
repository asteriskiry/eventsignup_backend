/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import fi.asteriski.eventsignup.filter.JwtFilter;
import fi.asteriski.eventsignup.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @NonNull
    private final UserService userService;
    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @NonNull
    private JwtFilter jwtFilter;
    @Value("${spring.profiles.active}")
    private String activeSpringProfile;

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
            .antMatchers(HttpMethod.GET, "/api/signup/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        switch (activeSpringProfile) {
            case ("dev") -> http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs.yaml").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs.json").permitAll()
                .and()
                .csrf().disable().cors().disable();
            case ("prod") -> http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.yaml").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api-docs.json").hasRole("ADMIN");
        }
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
