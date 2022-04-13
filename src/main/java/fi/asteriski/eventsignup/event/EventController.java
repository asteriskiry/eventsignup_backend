/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService){
        this.eventService=eventService;
    }

    @GetMapping("/event/get/{eventId}")
    public Event getEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getEvent(eventId);
    }

    @GetMapping("/event/all/{user}")
    public List<Event> getAllEventsForUser(@PathVariable String user, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getAllEventsForUser(user);
    }

    @GetMapping("/event/participants/{eventId}")
    public List<Participant> getParticipants(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return eventService.getParticipants(eventId);
    }

    @PostMapping(value = "/event/create", consumes = "application/json")
    public void createEvent(@RequestBody Event event, @AuthenticationPrincipal User loggedInUser) {
        // During testing loggedInUser will be null.
        var user = loggedInUser != null ? loggedInUser.getUsername() : "testUser";
        eventService.createNewEvent(event, user);
    }

    @PutMapping("/event/edit")
    public void editEvent(@RequestBody Event event, @AuthenticationPrincipal User loggedInUser) {
        eventService.editExistingEvent(event);
    }

    @PutMapping("/event/archive/{eventId}")
    public void archiveEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        eventService.archiveEvent(eventId);
    }

    @DeleteMapping("/event/remove/{eventId}")
    public void removeEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        eventService.removeEventAndParticipants(eventId);
    }
}
