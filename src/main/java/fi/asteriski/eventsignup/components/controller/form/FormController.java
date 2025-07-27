/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.controller.form;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_FORM;

import fi.asteriski.eventsignup.components.dto.FormDto;
import fi.asteriski.eventsignup.components.service.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController(API_PATH_FORM)
public class FormController {

    private final FormService formService;

    @PutMapping("/update")
    public FormDto updateForm(@RequestBody final FormDto formDto) {
        return formService.updateForm(formDto);
    }

    @PostMapping("/create")
    public void createForm(@RequestBody final FormDto formDto) {
        formService.createForm(formDto);
    }

    @Operation(
            summary = "Get an event's form for signup purposes.",
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
    @GetMapping("/{formId}")
    public FormDto fetchForm(@PathVariable final UUID formId) {
        return formService.fetchForm(formId);
    }
}
