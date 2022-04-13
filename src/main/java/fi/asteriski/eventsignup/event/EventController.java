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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService){
        this.eventService=eventService;
    }

    @Operation(summary = "Get an event by its id.")
    @GetMapping("/event/get/{eventId}")
    public Event getEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "Get all events for a user.", parameters = {@Parameter(description = "User's id.")})
    @GetMapping("/event/all/{user}")
    public List<Event> getAllEventsForUser(@PathVariable String user, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getAllEventsForUser(user);
    }

    @Operation(summary = "Get participants for an event by event's id.")
    @GetMapping("/event/participants/{eventId}")
    public List<Participant> getParticipants(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getParticipants(eventId);
    }

    @Operation(summary = "Create a new event.")
    @PostMapping(value = "/event/create", consumes = "application/json")
    public void createEvent(@RequestBody Event event, @AuthenticationPrincipal User loggedInUser) {
        // During testing loggedInUser will be null.
        var user = loggedInUser != null ? loggedInUser.getUsername() : "testUser";
        eventService.createNewEvent(event, user);
    }

    @Operation(summary = "Edit an existing event.")
    @PutMapping("/event/edit")
    public void editEvent(@RequestBody Event event, @AuthenticationPrincipal User loggedInUser) {
        eventService.editExistingEvent(event);
    }

    @Operation(summary = "Archive an event.")
    @PutMapping("/event/archive/{eventId}")
    public void archiveEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        eventService.archiveEvent(eventId);
    }

    @Operation(summary = "Delete an event.")
    @DeleteMapping("/event/remove/{eventId}")
    public void removeEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        eventService.removeEventAndParticipants(eventId);
    }
}
