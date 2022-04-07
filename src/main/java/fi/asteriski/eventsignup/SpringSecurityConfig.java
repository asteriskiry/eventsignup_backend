/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

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
//@EnableMethodSecurity(securedEnabled = true)
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@AllArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
//            .anyRequest().permitAll();
            .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/event/create").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.POST, "/event/banner/add").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.GET, "/event/banner/**").permitAll()
            .antMatchers(HttpMethod.PUT, "/event/edit/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.DELETE, "/event/remove/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.GET, "/event/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.GET, "/signup/**").permitAll()
//            .anyRequest().permitAll()
//            .antMatchers(HttpMethod.GET, "/admin/**").authenticated()
            .and()
            .csrf().disable();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        String adminUserName = System.getenv("ADMIN_USERNAME");
//        auth.inMemoryAuthentication().withUser("user").password(bCryptPasswordEncoder.encode("password")).roles("USER")
//            .and().withUser("admin").password(bCryptPasswordEncoder.encode("password")).roles("ADMIN", "USER");
//    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(bCryptPasswordEncoder);
    }

}
