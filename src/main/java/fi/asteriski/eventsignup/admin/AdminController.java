/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import fi.asteriski.eventsignup.Constants;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(Constants.API_PATH_ADMIN)
public class AdminController {

    private AdminService adminService;

    @Operation(summary = "Gets all events for admin view.",
        parameters = {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All events.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Event.class))})
    })
    @GetMapping("event/all")
    public List<Event> getAllEvents(Authentication loggedInUser) {
        return adminService.getAllEvents();
    }

    @Operation(summary = "Get all events of a specific user to admin view.",
        parameters = {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All events for the specific user.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Event.class))})
    })
    @GetMapping("event/{userId}")
    public List<Event> getAllEventsForUser(@PathVariable String userId, Authentication loggedInUser) {
        return adminService.getAllEventsForUser(userId);
    }

    @Operation(summary = "Gets all participants for admin view.",
        parameters = {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All participants in all events.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Participant.class))})
    })
    @GetMapping("participants/all")
    public List<Participant> getAllParticipants(Authentication loggedInUser) {
        return adminService.getAllParticipants();
    }

    @Operation(summary = "Gets all participants for a specific event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All participants in the specific event.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Participant.class))})
    })
    @GetMapping("participants/{eventId}")
    public List<Participant> getAllParticipantsForEvent(@PathVariable String eventId, Authentication loggedInUser) {
        return adminService.getAllParticipantsForEvent(eventId);
    }
}
