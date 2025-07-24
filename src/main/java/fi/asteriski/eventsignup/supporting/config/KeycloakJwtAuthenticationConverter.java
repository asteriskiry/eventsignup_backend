package fi.asteriski.eventsignup.supporting.config;

import static fi.asteriski.eventsignup.supporting.utils.Constants.ROLE_ADMIN;
import static fi.asteriski.eventsignup.supporting.utils.Constants.ROLE_USER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (!(realmAccess instanceof Map)) {
            return new ArrayList<>();
        }

        Object roles = ((Map<String, Object>) realmAccess).get("roles");
        if (!(roles instanceof Collection)) {
            return new ArrayList<>();
        }

        return ((Collection<?>) roles)
                .stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .filter(this::isValidRole)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList());
    }

    private String getPrincipalClaimName(Jwt jwt) {
        Object preferredUsername = jwt.getClaims().get("preferred_username");
        if (preferredUsername instanceof String userName) {
            return userName;
        }

        Object email = jwt.getClaims().get("email");
        if (email instanceof String userEmail) {
            return userEmail;
        }

        return jwt.getSubject();
    }

    private boolean isValidRole(String role) {
        return ROLE_USER.equalsIgnoreCase(role) || ROLE_ADMIN.equalsIgnoreCase(role);
    }
}
