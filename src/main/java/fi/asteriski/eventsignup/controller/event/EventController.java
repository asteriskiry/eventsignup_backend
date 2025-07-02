/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.controller.event;

import static fi.asteriski.eventsignup.utils.Constants.API_PATH_EVENT;

import fi.asteriski.eventsignup.dto.EventDto;
import fi.asteriski.eventsignup.dto.EventsDto;
import fi.asteriski.eventsignup.dto.NewEventAndFormRequest;
import fi.asteriski.eventsignup.dto.UsersEvents;
import fi.asteriski.eventsignup.service.event.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(API_PATH_EVENT)
public class EventController {

    private EventService eventService;

    @Operation(summary = "Get latest, upcoming and past events.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Requested data.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EventsDto.class))
                        }),
                @ApiResponse(responseCode = "401", description = "Unauthorized."),
            })
    @GetMapping("/events")
    public EventsDto fetchEvents() {
        return eventService.fetchEvents();
    }

    @Operation(summary = "Get logged in user's events and their forms.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Requested data.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UsersEvents.class))
                        }),
                @ApiResponse(responseCode = "401", description = "Unauthorized."),
            })
    @GetMapping("/users-events")
    public UsersEvents fetchUsersEvents() {
        return eventService.fetchUsersEvents();
    }

    @Operation(
            summary = "Create a new event.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = NewEventAndFormRequest.class))
                            }))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Event creation successful."),
                @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PostMapping("/create")
    public void createNewEvent(@Valid @RequestBody final NewEventAndFormRequest eventDto) {
        eventService.createNewEvent(eventDto);
    }

    @Operation(
            summary = "Update an existing event.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = EventDto.class))
                            }))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Event updated successfully."),
                @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PutMapping("/update")
    public void updateEvent(@Valid @RequestBody final EventDto eventDto) {
        eventService.updateEvent(eventDto);
    }
}
