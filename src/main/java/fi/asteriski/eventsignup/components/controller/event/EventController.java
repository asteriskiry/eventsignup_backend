/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.controller.event;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_EVENT;

import fi.asteriski.eventsignup.components.dto.*;
import fi.asteriski.eventsignup.components.service.AdminService;
import fi.asteriski.eventsignup.components.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(API_PATH_EVENT)
public class EventController {

    private EventService eventService;
    private AdminService adminService;

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
                @ApiResponse(
                        responseCode = "201",
                        description = "Event creation successful.",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = EventWithFormsDto.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PostMapping("/create")
    public ResponseEntity<EventWithFormsDto> createNewEvent(@Valid @RequestBody final NewEventAndFormRequest eventDto) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(eventService.createNewEvent(eventDto));
    }

    @Operation(summary = "Get latest, upcoming and past events with their forms and participants.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Requested data.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MyEvents.class))
                        }),
                @ApiResponse(responseCode = "401", description = "Unauthorized."),
            })
    @GetMapping("/events")
    public MyEvents fetchEvents() {
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
        return eventService.fetchUsersEventsNForms();
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
    public EventDto updateEvent(@Valid @RequestBody final EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }
}
