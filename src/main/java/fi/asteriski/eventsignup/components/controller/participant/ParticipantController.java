package fi.asteriski.eventsignup.components.controller.participant;

import fi.asteriski.eventsignup.components.dto.FormDto;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import fi.asteriski.eventsignup.components.service.FormService;
import fi.asteriski.eventsignup.components.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_PARTICIPANT;

@AllArgsConstructor
@RestController(API_PATH_PARTICIPANT)
public class ParticipantController {

    private final ParticipantService participantService;

    @Operation(
        summary = "Get the names of all the participants who have signed up on a form",
        parameters = {
            @Parameter(name = "formId", description = "Form's id"),
        })
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "The participant names requested.",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ParticipantDto.class))
                }),
            @ApiResponse(responseCode = "404", description = "Form not found."),
        })
    @GetMapping("/forms/{formId}/participants")
    public List<ParticipantDto> fetchForm(@PathVariable final UUID formId) {
        return participantService.getAllByFormId(formId);
    }

    @Operation(
        summary = "Add a participant to the form",
        parameters = {
            @Parameter(name = "formId", description = "Form's id"),
        })
    @GetMapping("/forms/{formId}/signup")
    public ParticipantDto add(@PathVariable UUID formId,
                              @RequestBody @Valid ParticipantDto dto) {
        return participantService.addParticipant(formId, dto);
    }
}
