/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.controller.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchiveEventRequest;
import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.model.archiving.ArchivedEventResponse;
import fi.asteriski.eventsignup.model.archiving.RemoveArchivedEventsRequest;
import fi.asteriski.eventsignup.service.archiving.ArchivedEventService;
import fi.asteriski.eventsignup.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(Constants.API_PATH_ARCHIVE)
public class ArchivedEventController {

    private ArchivedEventService archivedEventService;

    @Operation(summary = "Get all archived events. Admin user only.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "All archived events. List can be empty.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ArchivedEventResponse.class))
                        }),
                @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @GetMapping("get/all")
    public List<ArchivedEventResponse> getAllArchiveEvents() {
        return archivedEventService.getAllArchivedEvents();
    }

    @Operation(
            summary = "Get all archived events for a specific user sorted by date archived (desc). Admin user only.",
            parameters = {@Parameter(name = "userId", description = "User's id whose data is wanted.")})
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "All archived events for the requested user. List can be empty.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ArchivedEventDto.class))
                        }),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @GetMapping("get/{userId}")
    public List<ArchivedEventDto> getAllArchivedEventsForUser(@PathVariable String userId) {
        return archivedEventService.getAllArchivedEventsForUser(userId);
    }

    @Operation(
            summary = "Delete archived events older than set date. Admin user only.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = RemoveArchivedEventsRequest.class))
                            }))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Delete was successful."),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @DeleteMapping("remove/all")
    public void removeArchivedEvents(@RequestBody RemoveArchivedEventsRequest request) {
        archivedEventService.removeArchivedEventsBeforeDate(request.dateLimit().toInstant());
    }

    @Operation(
            summary = "Remove a single archived event. Admin user only.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ArchiveEventRequest.class))
                            }))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Delete was successful."),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @DeleteMapping("remove")
    public void removeArchivedEvent(@RequestBody ArchiveEventRequest request) {
        archivedEventService.removeArchivedEvent(request.archivedEventId());
    }

    @Operation(
            summary = "Archive an event. Admin user only.",
            parameters = {
                @Parameter(
                        name = "loggedInUser",
                        description = "Not required. Automatically added currently logged in user.")
            },
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ArchiveEventRequest.class))
                            }))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Event archiving successful."),
                @ApiResponse(responseCode = "401", description = "Unauthorized."),
                @ApiResponse(responseCode = "404", description = "Unable to archive event. Event was not found.")
            })
    @PutMapping("event")
    public void archiveEvent(@RequestBody ArchiveEventRequest request, Locale usersLocale) {
        archivedEventService.archiveEvent(request.archivedEventId(), usersLocale);
    }
}
