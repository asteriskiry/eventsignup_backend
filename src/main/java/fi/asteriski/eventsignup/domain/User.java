/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Document("admin")
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;
    @NonNull // For lombok
    @NotNull // For openApi
    private String firstName;
    @NonNull // For lombok
    @NotNull // For openApi
    private String lastName;
    @NonNull // For lombok
    @NotNull // For openApi
    @Email
    private String email;
    @NonNull // For lombok
    @NotNull // For openApi
    private String password;
    @NonNull // For lombok
    @NotNull // For openApi
    @Indexed(unique = true)
    private String username;
    private Instant expirationDate;

    @Builder.Default
    private UserRole userRole = UserRole.ROLE_USER;
    @Builder.Default
    private Boolean locked = false;
    @Builder.Default
    private Boolean enabled = true;
    @Builder.Default
    @Transient
    private boolean isAdmin = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(simpleGrantedAuthority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return expirationDate == null || expirationDate.isAfter(Instant.now());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
