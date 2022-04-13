/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerUnitTest {

    private UserService userService;
    private UserController userController;
    private User user;
    private List<User> users;
    private String username = "testUser";

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    @DisplayName("Get all non-admin users.")
    void getAllUsers() {
        users = TestUtils.createListOfRandomUsers(username);
        when(userService.getAllNonAdminUsers()).thenReturn(users);
        assertInstanceOf(List.class, userController.getAllNonAdminUsers(null));
    }

    @Test
    @DisplayName("Add a user.")
    void addUser() {
        user = TestUtils.createRandomUser(username);
        var valueCapture = ArgumentCaptor.forClass(User.class);
        doNothing().when(userService).addUser(valueCapture.capture());
        userController.addUser(user, null);
        verify(userService).addUser(any(User.class));
        assertEquals(user, valueCapture.getValue());
    }
}
