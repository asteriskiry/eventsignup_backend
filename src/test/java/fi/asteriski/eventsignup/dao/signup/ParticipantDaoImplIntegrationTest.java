/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.signup;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.repo.signup.ParticipantRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
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
        var id = UUID.randomUUID();
        var participants = TestUtils.createRandomParticipants(id).stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var result = participantDao.countAllByEvent(id);

        assertEquals(participants.size(), result);
    }

    @Test
    void countAllByEvent_givenEventExistsAndHasNoParticipants_expectCountOfZero() {
        var result = participantDao.countAllByEvent(UUID.randomUUID());

        assertEquals(0, result);
    }

    @Test
    void deleteAllByEventId_givenEventsHasParticipants_expectThemToBeDeleted() {
        var id = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var participants = TestUtils.createRandomParticipants(id);
        participants.addAll(TestUtils.createRandomParticipants(id2));
        participantRepository.saveAll(
                participants.stream().map(ParticipantDto::toEntity).toList());

        var countBefore = participantRepository.count();
        participantDao.deleteAllByEventIds(List.of(id, id2));
        var countAfter = participantRepository.count();

        assertNotEquals(countBefore, countAfter);
        assertEquals(0, countAfter);
    }

    @Test
    void findAllByEvent_givenEventHasParticipants_expectNonEmptyList() {
        var id = UUID.randomUUID();
        var participants = TestUtils.createRandomParticipants(id).stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var result = participantDao.findAllByEvent(id);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(participantDto -> Objects.equals(id, participantDto.getEvent())));
    }

    @Test
    void findAllByEvent_givenEventHasNoParticipants_expectEmptyList() {
        var result = participantDao.findAllByEvent(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllByEvent_givenEventHasParticipants_expectThemToBeDeleted() {
        var id = UUID.randomUUID();
        var participants = TestUtils.createRandomParticipants(id).stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var countBefore = participantRepository.count();
        participantDao.deleteAllByEvent(id);
        var countAfter = participantRepository.count();

        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void deleteAllByEvent_givenEventHasNoParticipants_expectNothingToBeDeleted() {
        var id = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var participants = TestUtils.createRandomParticipants(id).stream()
                .map(ParticipantDto::toEntity)
                .toList();
        participantRepository.saveAll(participants);

        var countBefore = participantRepository.count();
        participantDao.deleteAllByEvent(id2);
        var countAfter = participantRepository.count();

        assertEquals(countBefore, countAfter);
    }

    @Test
    void save_givenValidDate_expectItToBeSaved() {
        var participant = TestUtils.createRandomParticipant(UUID.randomUUID());

        var countBefore = participantRepository.count();
        var result = participantDao.save(participant);
        var countAfter = participantRepository.count();

        assertNotNull(result.getId());
        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void findById_givenParticipantExists_expectNonEmptyOptional() {
        var participant = TestUtils.createRandomParticipant(UUID.randomUUID()).toEntity();
        participant = participantRepository.save(participant);

        var result = participantDao.findById(participant.getId());

        assertFalse(result.isEmpty());
        assertInstanceOf(ParticipantDto.class, result.get());
    }

    @Test
    void findById_givenParticipantDoesNotExists_expectEmptyOptional() {
        var result = participantDao.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_givenNullParticipantId_expectIllegalArgumentException() {
        assertThatThrownBy(() -> participantDao.findById(null)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteParticipantByEventAndId_givenParticipantWithThatEventExists_expectItToBeDeleted() {
        var id = UUID.randomUUID();
        var participant = TestUtils.createRandomParticipant(id).toEntity();
        participant = participantRepository.save(participant);

        var countBefore = participantRepository.count();
        participantDao.deleteParticipantByEventAndId(id, participant.getId());
        var countAfter = participantRepository.count();

        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void deleteParticipantByEventAndId_givenNoParticipantWithThatEventExists_expectNothingToBeDeleted() {
        var id = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var countBefore = participantRepository.count();
        participantDao.deleteParticipantByEventAndId(id, id2);
        var countAfter = participantRepository.count();

        assertEquals(countBefore, countAfter);
    }
}
