/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import fi.asteriski.eventsignup.event.EventRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class MongoTests {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        eventRepository.save(TestUtils.createRandomEvent());
        participantRepository.save(TestUtils.createRandomParticipant());
    }

    @Test
    public void shouldBeNotEmpty() {
        assertFalse(eventRepository.findAll().isEmpty());
        assertFalse(participantRepository.findAll().isEmpty());
    }


}
