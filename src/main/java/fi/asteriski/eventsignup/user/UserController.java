/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/admin/users/all")
    public List<User> getAllNonAdminUsers(@AuthenticationPrincipal User loggedInUser) {
        return userService.getAllNonAdminUsers();
    }

    @PostMapping(value = "/admin/users/add", consumes = "application/json")
    public void addUser(@RequestBody User user, @AuthenticationPrincipal User loggedInUser) {
        userService.addUser(user);
    }

    @PutMapping("/admin/users/edit/")
    public void editUser(@RequestBody User user, @AuthenticationPrincipal User loggedInUser) {
        userService.editUser(user);
    }

    @DeleteMapping("/admin/users/delete/{userId}")
    public void deleteUser(@PathVariable String userId, @AuthenticationPrincipal User loggedInUser) {
        userService.deleteUser(userId);
    }

    @DeleteMapping("/admin/users/delete/all")
    public void deleteAllNonAdminUsers(@AuthenticationPrincipal User loggedInUser) {
        userService.deleteAllNonAdminUsers();
    }
}
