/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import fi.asteriski.eventsignup.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Service
public final class AuthService {

    private static final Map<String, AuthCredentialsResponse> CACHE = new HashMap<>();

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    ResponseEntity<?> authenticateUser(AuthCredentialsRequest request) {
        try {
            var authenticate = authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                    )
                );

            var user = (User) authenticate.getPrincipal();
            var token = jwtUtil.generateToken(user);
            var authCredentials = AuthCredentialsResponse.builder()
                .isAdmin(Objects.equals(UserRole.ROLE_ADMIN.name(), user.getUserRole().name()))
                .token(token)
                .build();
            var id = Integer.valueOf(token.hashCode()).toString();
            CACHE.put(id, authCredentials);

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, String.format("/api/auth/creds/%s", id))
                .build();
        } catch (DisabledException | LockedException | BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    AuthCredentialsResponse getAuthCredentials(String id) {
        return CACHE.remove(id);
    }
}
