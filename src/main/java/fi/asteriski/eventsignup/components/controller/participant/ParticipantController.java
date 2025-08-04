package fi.asteriski.eventsignup.components.controller.participant;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_PARTICIPANT;

import fi.asteriski.eventsignup.components.dto.EventWithFormsDto;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import fi.asteriski.eventsignup.components.dto.ParticipantNameDto;
import fi.asteriski.eventsignup.components.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(API_PATH_PARTICIPANT)
@Log4j2
public class ParticipantController {

    private final ParticipantService participantService;

    @Operation(
            summary = "Add a participant to a form",
            parameters = {
                @Parameter(name = "formId", description = "Form's id"),
            })
    @PostMapping("/{formId}/signup")
    public ParticipantDto add(@PathVariable UUID formId, @RequestBody @Valid ParticipantDto dto) {
        log.info("Request received");
        return participantService.addParticipant(formId, dto);
    }

    @GetMapping("/{formId}/participants/names")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of participant names.",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ParticipantNameDto.class, type = "array"))),
                @ApiResponse(responseCode = "404", description = "Form not found.")
            })
    public List<ParticipantNameDto> fetchParticipantNames(@PathVariable UUID formId) {
        return participantService.findNamesByFormId(formId).stream()
                .map(ParticipantNameDto::new)
                .toList();
    }

    @Operation(
            summary = "Get all participant data on participants who have signed up on a form",
            parameters = {
                @Parameter(name = "formId", description = "Form's id"),
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The full participant data requested.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ParticipantDto.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Form not found."),
            })
    @GetMapping("/{formId}/participants")
    public List<ParticipantDto> fetchForm(@PathVariable final UUID formId) {
        return participantService.getAllByFormId(formId);
    }

    @Operation(
            summary = "Update participants.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ParticipantDto.class))
                            }))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Update successful.",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = EventWithFormsDto.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PutMapping("/update")
    public ResponseEntity<Void> updateParticipants(@RequestBody final List<ParticipantDto> participants) {
        participantService.update(participants);

        return ResponseEntity.status(201).build();
    }
}
