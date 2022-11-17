/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.SignupEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Locale;

@AllArgsConstructor
@RestController
public class SignupController {

    private SignupService signupService;

    @Operation(summary = "Get an event for signup purposes.", parameters =
        {@Parameter(name = "eventId", description = "Event's id"),
            @Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
            @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The event requested.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SignupEvent.class))})
    })
    @GetMapping("/signup/{eventId}")
    public SignupEvent getEventForSignup(@PathVariable String eventId, Locale usersLocale, ZoneId userTimeZone) {
        return signupService.getEventForSignUp(eventId, usersLocale, userTimeZone);
    }

    @Operation(summary = "Signup for an event (i.e. add a participant).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Participant.class))}),
    parameters = {@Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
        @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signup successful.")
    })
    @PostMapping(value = "/signup/{eventId}/add", consumes = "application/json")
    public void addParticipantToEvent(@PathVariable String eventId, @RequestBody Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        signupService.addParticipantToEvent(eventId, participant, usersLocale, userTimeZone);
    }

    @Operation(summary = "Cancel participation to an event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id where to cancel from."),
            @Parameter(name = "participantId", description = "Participant's id (who's cancelling)."),
        @Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
        @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancellation successful.")
    })
    @DeleteMapping("/signup/cancel/{eventId}/{participantId}")
    void removeParticipantFromEvent(@PathVariable String eventId, @PathVariable String participantId, Locale usersLocale, ZoneId userTimeZone) {
        signupService.removeParticipantFromEvent(eventId, participantId, usersLocale, userTimeZone);
    }
}
