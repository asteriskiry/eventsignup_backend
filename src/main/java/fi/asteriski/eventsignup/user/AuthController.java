/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    @Operation(summary = "Get needed login data so client side scripts can work.", parameters =
        {@Parameter(name = "id", description = "Id in cache the data was stored to (created during the post call).")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The requested data.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthCredentialsResponse.class))})
    })
    @GetMapping("creds/{id}")
    public AuthCredentialsResponse getCredentials(@PathVariable String id) {
        /*
        This method exists since you should not return any data from a POST request.
        If data is needed to be returned a redirect to a get request is issued.
        See: https://en.wikipedia.org/wiki/Post/Redirect/Get
         */
        return authService.getAuthCredentials(id);
    }

    @Operation(summary = "Try to authenticate a user.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthCredentialsRequest.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "303", description = "Authentication was successful. Do a get request to get data."),
        @ApiResponse(responseCode = "401", description = "Authentication was unsuccessful.")
    })
    @PostMapping(value = "authenticate", consumes = "application/json")
    public ResponseEntity<?> login(@RequestBody AuthCredentialsRequest request) {
        return authService.authenticateUser(request);
    }
}
