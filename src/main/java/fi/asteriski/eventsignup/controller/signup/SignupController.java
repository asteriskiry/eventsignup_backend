/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.controller.signup;

import static fi.asteriski.eventsignup.utils.Constants.API_PATH_SIGNUP;

import fi.asteriski.eventsignup.dto.EventDto;
import fi.asteriski.eventsignup.dto.FormDto;
import fi.asteriski.eventsignup.dto.ParticipantDto;
import fi.asteriski.eventsignup.service.signup.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(API_PATH_SIGNUP)
public class SignupController {

    private SignupService signupService;

    @Operation(
            summary = "Get an event for signup purposes.",
            parameters = {
                @Parameter(name = "eventId", description = "Event's id"),
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The event requested.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EventDto.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Event was already held."),
                @ApiResponse(
                        responseCode = "409",
                        description =
                                "Signup not started/signup already ended/event full. See the message in response for details.")
            })
    @GetMapping("/{eventId}")
    public EventDto fetchSignupEvent(@PathVariable final UUID eventId) {
        return signupService.fetchSignupEvent(eventId);
    }

    @Operation(
            summary = "Get an event for signup purposes.",
            parameters = {
                @Parameter(name = "formId", description = "Form's id"),
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The form requested.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FormDto.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Form not found."),
            })
    @GetMapping(" /form/{formId}")
    public FormDto fetchSignupForm(@PathVariable final UUID formId) {
        return signupService.fetchSignupForm(formId);
    }

    @PostMapping("/{eventId}/add")
    public void signupToAnEvent(
            @PathVariable final UUID eventId, @Valid @RequestBody final ParticipantDto participantDto) {
        signupService.signupForAnEvent(eventId, participantDto);
    }
}
