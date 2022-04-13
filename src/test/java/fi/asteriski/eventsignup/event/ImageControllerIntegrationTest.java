package fi.asteriski.eventsignup.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.asteriski.eventsignup.user.UserRepository;
import fi.asteriski.eventsignup.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static fi.asteriski.eventsignup.utils.TestUtils.getImageDataAsBytes;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageController.class)
class ImageControllerIntegrationTest {

    @Value("${root.path.bannerimg}")
    private String rootPath;
//    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ImageService imageService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private WebApplicationContext context;
    // These mock beans are needed since they are declared in EventsignupApplication class.
    @MockBean
    UserRepository userRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity()) // enable security for the mock set up
            .build();
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Get file name in json of a banner image.")
    void getBannerImagePath() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", "exists");
        performLogin();
        mockMvc.perform(get("/event/banner/exists")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(mapper.writeValueAsString(map), true));
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Get existing banner image file as byte[].")
    void getBannerImage() throws Exception {
        byte[] file = getImageDataAsBytes(rootPath);
        when(imageService.getBannerImage("testFile.jpg")).thenReturn(file);
        performLogin();
        mockMvc.perform(get("/event/banner/get/testFile.jpg")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
            .andExpect(content().bytes(file));
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Send a POST to add a banner image. Except 3xx redirect.")
    void addBannerImg() throws Exception {
        byte[] file = getImageDataAsBytes(rootPath);
        when(imageService.addBannerImage(file, "user")).thenReturn("fileName");
        performLogin();
        mockMvc.perform(post("/event/banner/add").content(file)).andExpect(status().is3xxRedirection());
    }

    private void performLogin() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().is3xxRedirection());
    }
}
