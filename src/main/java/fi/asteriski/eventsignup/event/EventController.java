/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

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
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(Constants.API_PATH_EVENT)
public class EventController {

    private EventService eventService;

    @Operation(summary = "Get an event by its id.", parameters =
        {@Parameter(name = "eventId", description = "Requested events id."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requested event.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Event.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized."),
        @ApiResponse(responseCode = "404", description = "Event not found.")
    })
    @GetMapping("get/{eventId}")
    public Event getEvent(@PathVariable String eventId, Authentication loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        return eventService.getEvent(eventId, usersLocale, Optional.empty());
    }

    @Operation(summary = "Get all events for a user.", parameters = {@Parameter(description = "User's id."),
        @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All events of the user. List can be empty.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Event.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @GetMapping("all/{user}")
    public List<Event> getAllEventsForUser(@PathVariable String user, Authentication loggedInUser) {
        return eventService.getAllEventsForUser(user);
    }

    @Operation(summary = "Get participants for an event by event's id.", parameters =
        {@Parameter(name = "eventId", description = "Event's id."),
        @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participants of the requested event. List can be empty.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Participant.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @GetMapping("participants/{eventId}")
    public List<Participant> getParticipants(@PathVariable String eventId, Authentication loggedInUser) {
        return eventService.getParticipants(eventId);
    }

    @Operation(summary = "Create a new event.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user."),
            @Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
            @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")},
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event creation successful."),
        @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createEvent(@RequestBody Event event, Authentication loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        // During testing loggedInUser will be null.
        eventService.createNewEvent(event, loggedInUser, usersLocale, userTimeZone);
    }

    @Operation(summary = "Edit an existing event.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user."),
            @Parameter(name = "usersLocale", description = "Automatically inserted based on request headers."),
            @Parameter(name = "userTimeZone", description = "Automatically inserted based on request headers.")},
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event editing successful."),
        @ApiResponse(responseCode = "401", description = "Unauthorized."),
        @ApiResponse(responseCode = "404", description = "Unable to edit. Old event not found.")
    })
    @PutMapping("edit")
    public void editEvent(@RequestBody Event event, Authentication loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        eventService.editExistingEvent(event, loggedInUser, usersLocale, userTimeZone);
    }

    @Operation(summary = "Delete an event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event deleted successfully."),
        @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @DeleteMapping("remove/{eventId}")
    public void removeEvent(@PathVariable String eventId, Authentication loggedInUser) {
        eventService.removeEventAndParticipants(eventId);
    }
}
