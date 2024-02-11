/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.signup;

import static org.junit.jupiter.api.Assertions.*;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.repo.signup.ParticipantRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ParticipantDaoImplIntegrationTest {

    @Autowired
    private ParticipantRepository participantRepository;

    private ParticipantDaoImpl participantDao;

    @BeforeEach
    void setUp() {
        participantDao = new ParticipantDaoImpl(participantRepository);
    }

    @AfterEach
    void tearDown() {
        participantRepository.deleteAll();
    }

    @Test
    void countAllByEvent_givenEventExistsAndHasParticipants_expectCountGreaterThanZero() {
        var participants = TestUtils.createRandomParticipants("123").stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var result = participantDao.countAllByEvent("123");

        assertEquals(participants.size(), result);
    }

    @Test
    void countAllByEvent_givenEventExistsAndHasNoParticipants_expectCountOfZero() {
        var result = participantDao.countAllByEvent("exists");

        assertEquals(0, result);
    }

    @Test
    void deleteAllByEventId_givenEventsHasParticipants_expectThemToBeDeleted() {
        var participants = TestUtils.createRandomParticipants("123");
        participants.addAll(TestUtils.createRandomParticipants("456"));
        participantRepository.saveAll(
                participants.stream().map(ParticipantDto::toEntity).toList());

        var countBefore = participantRepository.count();
        participantDao.deleteAllByEventIds(List.of("123", "456"));
        var countAfter = participantRepository.count();

        assertNotEquals(countBefore, countAfter);
        assertEquals(0, countAfter);
    }

    @Test
    void findAllByEvent_givenEventHasParticipants_expectNonEmptyList() {
        var participants = TestUtils.createRandomParticipants("123").stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var result = participantDao.findAllByEvent("123");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(participantDto -> Objects.equals("123", participantDto.getEvent())));
    }

    @Test
    void findAllByEvent_givenEventHasNoParticipants_expectEmptyList() {
        var result = participantDao.findAllByEvent("123");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllByEvent_givenEventHasParticipants_expectThemToBeDeleted() {
        var participants = TestUtils.createRandomParticipants("123").stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var countBefore = participantRepository.count();
        participantDao.deleteAllByEvent("123");
        var countAfter = participantRepository.count();

        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void deleteAllByEvent_givenEventHasNoParticipants_expectNothingToBeDeleted() {
        var participants = TestUtils.createRandomParticipants("123").stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var countBefore = participantRepository.count();
        participantDao.deleteAllByEvent("456");
        var countAfter = participantRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @Test
    void save_givenValidDate_expectItToBeSaved() {
        var participant = TestUtils.createRandomParticipant("123");

        var countBefore = participantRepository.count();
        var result = participantDao.save(participant);
        var countAfter = participantRepository.count();

        assertNotNull(result.getId());
        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void findById_givenParticipantExists_expectNonEmptyOptional() {
        var participant = TestUtils.createRandomParticipant("123").toEntity();
        participant = participantRepository.save(participant);

        var result = participantDao.findById(participant.getId());

        assertFalse(result.isEmpty());
        assertInstanceOf(ParticipantDto.class, result.get());
    }

    @Test
    void findById_givenParticipantDoesNotExists_expectEmptyOptional() {
        var result = participantDao.findById("123");

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_givenNullParticipantId_expectIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> participantDao.findById(null));
    }

    @Test
    void deleteParticipantByEventAndId_givenParticipantWithThatEventExists_expectItToBeDeleted() {
        var participant = TestUtils.createRandomParticipant("123").toEntity();
        participant = participantRepository.save(participant);

        var countBefore = participantRepository.count();
        participantDao.deleteParticipantByEventAndId("123", participant.getId());
        var countAfter = participantRepository.count();

        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void deleteParticipantByEventAndId_givenNoParticipantWithThatEventExists_expectNothingToBeDeleted() {
        var countBefore = participantRepository.count();
        participantDao.deleteParticipantByEventAndId("123", "456");
        var countAfter = participantRepository.count();

        assertEquals(countBefore, countAfter);
    }
}
