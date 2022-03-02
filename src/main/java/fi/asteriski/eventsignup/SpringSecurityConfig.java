package fi.asteriski.eventsignup;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    // Secure the endpoins with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            //HTTP Basic authentication
            .httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/event/**").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/event/create").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/event/banner/add").hasRole("USER")
            .antMatchers(HttpMethod.PUT, "/event/edit/**").hasRole("USER")
            .antMatchers(HttpMethod.DELETE, "/event/remove/**").hasRole("USER")
            .antMatchers(HttpMethod.DELETE, "/event/remove/**").hasRole("ADMIN")
            .and()
            .csrf().disable()
            .formLogin().disable();
    }
}
