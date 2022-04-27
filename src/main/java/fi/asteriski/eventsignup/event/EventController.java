/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService){
        this.eventService=eventService;
    }

    @Operation(summary = "Get an event by its id.", parameters =
        {@Parameter(name = "eventId", description = "Requested events id."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requested event.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Event.class))}),
        @ApiResponse(responseCode = "404", description = "Event not found.")
    })
    @GetMapping("/event/get/{eventId}")
    public Event getEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "Get all events for a user.", parameters = {@Parameter(description = "User's id."),
        @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All events of the user.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Event.class))})
    })
    @GetMapping("/event/all/{user}")
    public List<Event> getAllEventsForUser(@PathVariable String user, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getAllEventsForUser(user);
    }

    @Operation(summary = "Get participants for an event by event's id.", parameters =
        {@Parameter(name = "eventId", description = "Event's id."),
        @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participants of the requested event.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Participant.class))})
    })
    @GetMapping("/event/participants/{eventId}")
    public List<Participant> getParticipants(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getParticipants(eventId);
    }

    @Operation(summary = "Create a new event.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")},
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Participant.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event creation successful.")
    })
    @PostMapping(value = "/event/create", consumes = "application/json")
    public void createEvent(@RequestBody Event event, @AuthenticationPrincipal User loggedInUser) {
        // During testing loggedInUser will be null.
        eventService.createNewEvent(event, loggedInUser);
    }

    @Operation(summary = "Edit an existing event.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")},
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event editing successful.")
    })
    @PutMapping("/event/edit")
    public void editEvent(@RequestBody Event event, @AuthenticationPrincipal User loggedInUser) {
        eventService.editExistingEvent(event, loggedInUser);
    }

    @Operation(summary = "Archive an event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Event.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event archiving successful.")
    })
    @PutMapping("/event/archive/{eventId}")
    public void archiveEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        eventService.archiveEvent(eventId);
    }

    @Operation(summary = "Delete an event.", parameters =
        {@Parameter(name = "eventId", description = "Event's id."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event deleted successfully.")
    })
    @DeleteMapping("/event/remove/{eventId}")
    public void removeEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        eventService.removeEventAndParticipants(eventId);
    }
}
