package fi.asteriski.eventsignup.components.controller.admin;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_ADMIN;

import fi.asteriski.eventsignup.components.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_PATH_ADMIN)
@AllArgsConstructor
public class AdminController {

    private AdminService adminService;

    @PostMapping("/wipe-db")
    public ResponseEntity<Void> wipeDatabase() {
        adminService.wipeDatabase();
        return ResponseEntity.noContent().build();
    }
}
