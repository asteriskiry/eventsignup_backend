/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import fi.asteriski.eventsignup.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Configures end point security.
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//            HTTP Basic authentication
            .httpBasic()
            .and()
            .formLogin().successForwardUrl("/redirect")
            .and()
            .logout().deleteCookies()
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
            .and()
            .csrf().disable();
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

}
