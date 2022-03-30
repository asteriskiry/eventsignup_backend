/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService){
        this.eventService=eventService;
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/event/get/{eventId}")
    public Event getEvent(@PathVariable String eventId) {
        return eventService.getEvent(eventId);
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/event/all/{user}")
    public List<Event> getAllEventsForUser(@PathVariable String user) {
        return eventService.getAllEventsForUser(user);
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/event/participants/{eventId}")
    public List<Participant> getParticipants(@PathVariable String eventId) {
        return eventService.getParticipants(eventId);
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping(value = "/event/create", consumes = "application/json")
    public void createEvent(@RequestBody Event event, Principal principal) {
        // FIXME principal can be null
        var user = principal != null ? principal.getName() : "user";
        eventService.createNewEvent(event, user);
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/event/edit")
    public void editEvent(@RequestBody Event event) {
        eventService.editExistingEvent(event);
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/event/archive/{eventId}")
    public void archiveEvent(@PathVariable String eventId) {
        eventService.archiveEvent(eventId);
    }

    @DeleteMapping("/event/remove/{eventId}")
    public void removeEvent(@PathVariable String eventId) {
        eventService.removeEventAndParticipants(eventId);
    }
}
