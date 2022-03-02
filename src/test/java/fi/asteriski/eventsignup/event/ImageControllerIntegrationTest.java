package fi.asteriski.eventsignup.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static fi.asteriski.eventsignup.utils.TestUtils.getImageDataAsBytes;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageController.class)
class ImageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ImageService imageService;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Get file name in json of a banner image.")
    void getBannerImagePath() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", "exists");
        mockMvc.perform(get("/event/banner/exists")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(mapper.writeValueAsString(map), true));
    }

    @Test
    @DisplayName("Get existing banner image file as byte[].")
    void getBannerImage() throws Exception {
        byte[] file = getImageDataAsBytes();
        when(imageService.getBannerImage("testFile.jpg")).thenReturn(file);
        mockMvc.perform(get("/event/banner/get/testFile.jpg")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
            .andExpect(content().bytes(file));
    }

    @Test
    @DisplayName("Send a POST to add a banner image. Except 3xx redirect.")
    void addBannerImg() throws Exception {
        byte[] file = getImageDataAsBytes();
        when(imageService.addBannerImage(file, "user")).thenReturn("fileName");
        mockMvc.perform(post("/event/banner/add").content(file)).andExpect(status().is3xxRedirection());
    }

}
