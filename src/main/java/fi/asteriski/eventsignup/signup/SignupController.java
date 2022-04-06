/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import org.springframework.web.bind.annotation.*;

@RestController
public class SignupController {

    private SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping("/signup/{eventId}")
    public Event getEventForSignup(@PathVariable String eventId) {
        return signupService.getEventForSignUp(eventId);
    }

    @PostMapping(value = "/signup/{eventId}/add", consumes = "application/json")
    public void addParticipantToEvent(@PathVariable String eventId, @RequestBody Participant participant) {
        signupService.addParticipantToEvent(eventId, participant);
    }

    @DeleteMapping("/signup/cancel/{eventId}/{participantId}")
    void removeParticipantFromEvent(@PathVariable String eventId, @PathVariable String participantId) {
        signupService.removeParticipantFromEvent(eventId, participantId);
    }
}
