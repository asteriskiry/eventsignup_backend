/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import fi.asteriski.eventsignup.event.EventRepository;
import fi.asteriski.eventsignup.user.UserRepository;
import fi.asteriski.eventsignup.user.UserService;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class MongoTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ParticipantRepository participantRepository;
    // These mock beans are needed since they are declared in EventsignupApplication class.
    @MockBean
    UserRepository userRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    UserService userService;

    @BeforeEach
    void setUp() {
        eventRepository.save(TestUtils.createRandomEvent(null));
        participantRepository.save(TestUtils.createRandomParticipant(null));
    }

    @Test
    public void shouldBeNotEmpty() {
        assertFalse(eventRepository.findAll().isEmpty());
        assertFalse(participantRepository.findAll().isEmpty());
    }


}
