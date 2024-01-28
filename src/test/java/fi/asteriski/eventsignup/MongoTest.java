/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import fi.asteriski.eventsignup.repo.event.EventRepository;
import fi.asteriski.eventsignup.repo.signup.ParticipantRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataMongoTest
class MongoTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        eventRepository.save(TestUtils.createRandomEvent(null).toEntity());
        participantRepository.save(TestUtils.createRandomParticipant(null).toEntity());
    }

    @Test
    void shouldBeNotEmpty() {
        assertFalse(eventRepository.findAll().isEmpty());
        assertFalse(participantRepository.findAll().isEmpty());
    }


}
