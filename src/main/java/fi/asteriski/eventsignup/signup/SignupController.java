/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class SignupController {

    private SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @Operation(summary = "Get an event for signup purposes.", parameters =
        {@Parameter(name = "eventId", description = "Event's id")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The event requested.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))})
    })
    @GetMapping("/signup/{eventId}")
    public Event getEventForSignup(@PathVariable String eventId) {
        return signupService.getEventForSignUp(eventId);
    }

    @Operation(summary = "Signup for an event (i.e. add a participant).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Participant.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signup successful.")
    })
    @PostMapping(value = "/signup/{eventId}/add", consumes = "application/json")
    public void addParticipantToEvent(@PathVariable String eventId, @RequestBody Participant participant) {
        signupService.addParticipantToEvent(eventId, participant);
    }

    @Operation(summary = "Cancel participation to an event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id where to cancel from."),
            @Parameter(name = "participantId", description = "Participant's id (who's cancelling).")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancellation successful.")
    })
    @DeleteMapping("/signup/cancel/{eventId}/{participantId}")
    void removeParticipantFromEvent(@PathVariable String eventId, @PathVariable String participantId) {
        signupService.removeParticipantFromEvent(eventId, participantId);
    }
}
