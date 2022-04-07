/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class AdminController {

    private AdminService adminService;

    @GetMapping("/admin/event/all")
    public List<Event> getAllEvents(@AuthenticationPrincipal User loggedInUser) {
        return adminService.getAllEvents();
    }

    @GetMapping("/admin/event/{userId}")
    public List<Event> getAllEventsForUser(@PathVariable String userId, @AuthenticationPrincipal User loggedInUser) {
        return adminService.getAllEventsForUser(userId);
    }

    @GetMapping("/admin/participants/all")
    public List<Participant> getAllParticipants(@AuthenticationPrincipal User loggedInUser) {
        return adminService.getAllParticipants();
    }

    @GetMapping("/admin/participants/{eventId}")
    public List<Participant> getAllParticipantsForEvent(@PathVariable String eventId, @AuthenticationPrincipal User loggedInUser) {
        return adminService.getAllParticipantsForEvent(eventId);
    }
}
