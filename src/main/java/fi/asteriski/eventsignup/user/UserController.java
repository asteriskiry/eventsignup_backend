/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class UserController {

    private UserService userService;

    @Operation(summary = "Get all non-admin users for admin view.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All participants in the specific event.",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = User.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthenticated.")
    })
    @GetMapping("users/all")
    public List<User> getAllNonAdminUsers(@AuthenticationPrincipal User loggedInUser) {
        return userService.getAllNonAdminUsers();
    }

    @Operation(summary = "Add a new user to the system.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "New user adding successful."),
        @ApiResponse(responseCode = "401", description = "Unauthenticated.")
    })
    @PostMapping(value = "users/add", consumes = "application/json")
    public void addUser(@RequestBody User user, @AuthenticationPrincipal User loggedInUser) {
        userService.addUser(user);
    }

    @Operation(summary = "Edit an existing users.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))}))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User editing successful."),
        @ApiResponse(responseCode = "401", description = "Unauthenticated.")
    })
    @PutMapping("users/edit/")
    public void editUser(@RequestBody User user, @AuthenticationPrincipal User loggedInUser) {
        userService.editUser(user);
    }

    @Operation(summary = "Deletes an user by their id.", parameters =
        {@Parameter(name = "userId", description = "Id of the user to delete."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delete was successful."),
        @ApiResponse(responseCode = "401", description = "Unauthenticated.")
    })
    @DeleteMapping("users/delete/{userId}")
    public void deleteUser(@PathVariable String userId, @AuthenticationPrincipal User loggedInUser) {
        userService.deleteUser(userId);
    }

    @Operation(summary = "Delete ALL non-admin users.", parameters =
        {@Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delete was successful."),
        @ApiResponse(responseCode = "401", description = "Unauthenticated.")
    })
    @DeleteMapping("users/delete/all")
    public void deleteAllNonAdminUsers(@AuthenticationPrincipal User loggedInUser) {
        userService.deleteAllNonAdminUsers();
    }
}
