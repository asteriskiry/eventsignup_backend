/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @Operation(summary = "Get all non-admin users for admin view.")
    @GetMapping("/admin/users/all")
    public List<User> getAllNonAdminUsers(@AuthenticationPrincipal User loggedInUser) {
        return userService.getAllNonAdminUsers();
    }

    @Operation(summary = "Add a new user to the system.")
    @PostMapping(value = "/admin/users/add", consumes = "application/json")
    public void addUser(@RequestBody User user, @AuthenticationPrincipal User loggedInUser) {
        userService.addUser(user);
    }

    @Operation(summary = "Edit an existing users.")
    @PutMapping("/admin/users/edit/")
    public void editUser(@RequestBody User user, @AuthenticationPrincipal User loggedInUser) {
        userService.editUser(user);
    }

    @Operation(summary = "Delete an user by their id.")
    @DeleteMapping("/admin/users/delete/{userId}")
    public void deleteUser(@PathVariable String userId, @AuthenticationPrincipal User loggedInUser) {
        userService.deleteUser(userId);
    }

    @Operation(summary = "Delete ALL non-admin users.")
    @DeleteMapping("/admin/users/delete/all")
    public void deleteAllNonAdminUsers(@AuthenticationPrincipal User loggedInUser) {
        userService.deleteAllNonAdminUsers();
    }
}
