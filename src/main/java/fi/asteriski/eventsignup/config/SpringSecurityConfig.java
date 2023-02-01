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
@AllArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtFilter jwtFilter;

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
            .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/event/create").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.POST, "/event/banner/add").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.GET, "/event/banner/**").permitAll()
            .antMatchers(HttpMethod.PUT, "/event/edit/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.DELETE, "/event/remove/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.GET, "/event/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.GET, "/swagger-ui/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api-docs/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api-docs.yaml").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api-docs.json").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/signup/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf().disable().cors().disable(); // Don't disable these in prod
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
