/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.domain.UserRole;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("unchecked")
@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    private MockMvc mockMvc;
    @MockBean
    UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private WebApplicationContext context;
    // These mock beans are needed since they are declared in EventsignupApplication class.
    @MockBean
    UserRepository userRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    private String username = "testUser";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity()) // enable security for the mock set up
            .build();
        // This is needed so Java 8 date/time classes are supported by the mapper.
        this.mapper.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @WithMockUser(value = "test", password = "pass", roles = {"ADMIN"})
    @DisplayName("Get all users from admin interface.")
    void getAllNonAdminUsers() throws Exception {
       var users = TestUtils.createListOfRandomUsers(username);
       var usersAsJson = mapper.writeValueAsString(users);
       when(userService.getAllNonAdminUsers()).thenReturn(users);
       performLogin();
       var results = mockMvc.perform(get("/admin/users/all")).andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
           .andExpect(content().json(usersAsJson, true))
           .andReturn();
        var value = mapper.readValue(results.getResponse().getContentAsString(), List.class);
       verify(userService).getAllNonAdminUsers();
       assertTrue(value.stream().allMatch(user -> UserRole.ROLE_USER.name().equals(((Map<?, ?>) user).get("userRole"))));
    }

    private void performLogin() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().is3xxRedirection());
    }
}
