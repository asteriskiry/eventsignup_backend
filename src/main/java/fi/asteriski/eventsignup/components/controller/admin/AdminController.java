package fi.asteriski.eventsignup.components.controller.admin;

import fi.asteriski.eventsignup.components.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_ADMIN;

@RestController
@RequestMapping(API_PATH_ADMIN)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/wipe-db")
    public ResponseEntity<Void> wipeDatabase() {
        adminService.wipeDatabase();
        return ResponseEntity.noContent().build();
    }
}
