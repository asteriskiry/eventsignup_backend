package fi.asteriski.eventsignup.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.asteriski.eventsignup.utils.TestUtils;
import fi.asteriski.eventsignup.domain.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private Event event;


    @BeforeEach
    void setUp() {
        this.event = TestUtils.createRandomEvent();
        // This is needed so Java 8 date/time classes are supported by the mapper.
        this.mapper.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @DisplayName("Try to get non existent event.")
    void getNonExistentEvent() throws Exception {
        mockMvc.perform(get("/event/get/foobar")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get empty list of events for user with no events.")
    void getAllEventsForUserWithNoEvents() throws Exception {
        mockMvc.perform(get("/event/all/testuser")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Get list of participants to existing event.")
    void getParticipantsToEventWithParticipants() throws Exception {
        // FIXME mock an actual event and use that id!
        mockMvc.perform(get("/event/participants/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            // FIXME
            .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Get empty list of participants to existing event with no participants.")
    void getParticipantsToEventWithNoParticipants() throws Exception {
        // FIXME mock an actual event and use that id!
        mockMvc.perform(get("/event/participants/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Create a new valid Event.")
    void createNewValidEvent() throws Exception {
        var jsonString = mapper.writeValueAsString(event);
        mockMvc.perform(post("/event/create/").contentType(MediaType.APPLICATION_JSON)
            .content(jsonString)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Try to create a new Event with invalid data.")
    void tryToCreateNewEventWithInvalidData() throws Exception {
        mockMvc.perform(post("/event/create/").contentType(MediaType.APPLICATION_JSON)
            .content("{}")).andExpect(status().isBadRequest());
    }

//    @Test
//    void editEvent() throws Exception {
//        event.setId("123");
//        var jsonString = mapper.writeValueAsString(event);
//        mockMvc.perform(put("/event/edit/").contentType(MediaType.APPLICATION_JSON)
//            .content(jsonString)).andExpect(status().isOk());
//    }
//
//    @Test
//    void archiveEvent() {
//    }
//
//    @Test
//    void removeEventAndParticipants() {
//    }

}
