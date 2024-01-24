/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.Constants;
import fi.asteriski.eventsignup.domain.SignupEvent;
import fi.asteriski.eventsignup.domain.signup.Participant;
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
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
@RestController
@RequestMapping(Constants.API_PATH_SIGNUP)
public class SignupController {

    private SignupService signupService;

    @Operation(summary = "Get an event for signup purposes.", parameters =
        {@Parameter(name = "eventId", description = "Event's id"),
            @Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
            @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The event requested.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SignupEvent.class))}),
        @ApiResponse(responseCode = "404", description = "Event was already held."),
        @ApiResponse(responseCode = "409", description = "Signup not started/signup already ended/event full. See the message in response for details.")
    })
    @GetMapping("{eventId}")
    public SignupEvent getEventForSignup(@PathVariable String eventId, Locale usersLocale, ZoneId userTimeZone) {
        return signupService.getEventForSignUp(eventId, usersLocale, userTimeZone);
    }

    @Operation(summary = "Get a list of upcoming events at to selected days.", parameters =
        {@Parameter(name = "days", description = "How many days into the future events are wanted.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The events requested.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SignupEvent.class))})
    })
    @GetMapping("upcomingEvents/{days}")
    public List<SignupEvent> getUpcomingEvents(@PathVariable String days) {
        return signupService.getUpcomingEvents(days);
    }

    @Operation(summary = "Signup for an event (i.e. add a participant).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Participant.class))}),
    parameters = {@Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
        @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signup successful."),
        @ApiResponse(responseCode = "404", description = "Event not found."),
    })
    @PostMapping(value = "{eventId}/add", consumes = "application/json")
    public void addParticipantToEvent(@PathVariable String eventId, @RequestBody Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        signupService.addParticipantToEvent(eventId, participant, usersLocale, userTimeZone);
    }

    @Operation(summary = "Cancel participation to an event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id where to cancel from."),
            @Parameter(name = "participantId", description = "Participant's id (who's cancelling)."),
        @Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
        @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancellation successful."),
        @ApiResponse(responseCode = "404", description = "Event not found"),
    })
    @DeleteMapping("cancel/{eventId}/{participantId}")
    void removeParticipantFromEvent(@PathVariable String eventId, @PathVariable String participantId, Locale usersLocale, ZoneId userTimeZone) {
        signupService.removeParticipantFromEvent(eventId, participantId, usersLocale, userTimeZone);
    }
}
