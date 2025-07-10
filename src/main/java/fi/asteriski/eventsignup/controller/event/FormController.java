/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.controller.event;

import static fi.asteriski.eventsignup.utils.Constants.API_PATH_FORM;

import fi.asteriski.eventsignup.dto.EventDto;
import fi.asteriski.eventsignup.dto.FormDto;
import fi.asteriski.eventsignup.service.event.FormService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController(API_PATH_FORM)
public class FormController {

    private final FormService formService;

    @PutMapping("/update")
    public void updateForm(@RequestBody final FormDto formDto) {
        formService.updateForm(formDto);
    }

    @PostMapping("/create")
    public void createForm(@RequestBody final FormDto formDto) {
        formService.createForm(formDto);
    }

    @GetMapping("/{formId}")
    public FormDto fetchForm(@PathVariable final UUID formId) {
        return formService.fetchForm(formId);
    }
}
