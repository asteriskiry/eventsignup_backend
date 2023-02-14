/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class AuthCredentialsResponse {

    @NonNull
    private Boolean isAdmin;
    @NonNull
    private String token;
}
