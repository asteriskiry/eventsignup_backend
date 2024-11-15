/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.controller.admin;

import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.service.admin.AdminService;
import fi.asteriski.eventsignup.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(Constants.API_PATH_ADMIN)
public class AdminController {

    private AdminService adminService;

    @Operation(summary = "Gets all events for admin view.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "All events.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EventDto.class))
                        })
            })
    @GetMapping("event/all")
    public List<EventDto> getAllEvents() {
        return adminService.getAllEvents();
    }

    @Operation(summary = "Get all events of a specific user to admin view.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "All events for the specific user.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EventDto.class))
                        })
            })
    @GetMapping("event/{userId}")
    public List<EventDto> getAllEventsForUser(@PathVariable String userId) {
        return adminService.getAllEventsForUser(userId);
    }

    @Operation(summary = "Gets all participants for admin view.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "All participants in all events.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ParticipantDto.class))
                        })
            })
    @GetMapping("participants/all")
    public List<ParticipantDto> getAllParticipants() {
        return adminService.getAllParticipants();
    }

    @Operation(
            summary = "Gets all participants for a specific event.",
            parameters = {@Parameter(name = "eventId", description = "Event's id.")})
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "All participants in the specific event.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ParticipantDto.class))
                        })
            })
    @GetMapping("participants/{eventId}")
    public List<ParticipantDto> getAllParticipantsForEvent(@PathVariable UUID eventId) {
        return adminService.getAllParticipantsForEvent(eventId);
    }
}
