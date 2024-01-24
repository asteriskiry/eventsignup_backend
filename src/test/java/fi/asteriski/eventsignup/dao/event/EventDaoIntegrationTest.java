/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.dao.event;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.event.EventEntity;
import fi.asteriski.eventsignup.repo.event.EventRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class EventDaoIntegrationTest {

    private static final Random rnd = new Random();

    @Autowired
    private EventRepository eventRepository;
    private EventDaoImpl eventDao;
    private final String testUser = "testUser";

    @BeforeEach
    void setUp() {
        eventDao = new EventDaoImpl(eventRepository);
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
    }

    @Test
    void findById_givenEventExists_expectResult() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toEntity()).toDto();

        var result = eventDao.findById(event.getId());

        assertTrue(result.isPresent());
        assertEquals(event.getId(), result.get().getId());
    }

    @Test
    void findById_givenEventDoesNotExist_expectEmptyOptional() {
        var result = eventDao.findById("not_exist");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByOwner_givenOwnersEventsExist_expectNonEmptyList() {
        eventRepository.saveAll(TestUtils.getRandomEvents(testUser).stream().map(EventDto::toEntity).toList());

        var result = eventDao.findAllByOwner(testUser);

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllByOwner_givenOwnersEventsDoNotExist_expectEmptyList() {
        eventRepository.saveAll(TestUtils.getRandomEvents(testUser).stream().map(EventDto::toEntity).toList());

        var result = eventDao.findAllByOwner("otherUser");

        assertTrue(result.isEmpty());
    }

    @Test
    void save_givenValidData_expectItToBeSavedToDatabase() {
        var event = TestUtils.createRandomEvent(testUser);

        var result = eventDao.save(event);

        assertNotNull(result.getId());
        assertNotEquals(event.getId(), result.getId());
    }

    @Test
    void deleteById_givenDataExistInDatabase_expectDataNotExistInDatabase() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toEntity()).toDto();
        var countBefore = eventRepository.count();

        eventDao.deleteById(event.getId());
        var countAfter = eventRepository.count();

        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void existsById_givenDataExists_expectTrue() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toEntity()).toDto();

        var result = eventDao.existsById(event.getId());

        assertTrue(result);
    }

    @Test
    void existsById_givenDataDoesNotExist_expectFalse() {
        var result = eventDao.existsById("notExist");

        assertFalse(result);
    }

    @Test
    void findAllByStartDateIsBeforeOrEndDateIsBefore_givenDataExistsInDatabase_expectNonEmptyList() {
        var events = TestUtils.getRandomEvents(testUser);
        eventRepository.saveAll(events.stream().map(EventDto::toEntity).toList());
        var startDate = events.get(0).getStartDate();
        var endDate = getEndDate(startDate, events);

        var result = eventDao.findAllByStartDateIsBeforeOrEndDateIsBefore(startDate.toInstant(), endDate.toInstant());

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllByStartDateIsBeforeOrEndDateIsBefore_givenDataDoesNotExistInDatabase_expectEmptyList() {
        eventRepository.deleteAll();
        var events = TestUtils.getRandomEvents(testUser);
        var startDate = events.get(0).getStartDate();
        var endDate = getEndDate(startDate, events);

        var result = eventDao.findAllByStartDateIsBeforeOrEndDateIsBefore(startDate.toInstant(), endDate.toInstant());

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllByIds_givenDataExistsInDatabase_expectDataNotToExistInDatabase() {
        var events = eventRepository.saveAll(TestUtils.getRandomEvents(testUser).stream().map(EventDto::toEntity).toList());
        var countBefore = eventRepository.count();

        eventDao.deleteAllByIds(events.stream().map(EventEntity::getId).toList());
        var countAfter = eventRepository.count();

        assertEquals(events.size() - countBefore, countAfter);
    }

    @Test
    void deleteAllByIds_givenDataDoesNotExistInDatabase_doesNotThrowAnyException() {
        var eventIds = TestUtils.createRandomIdList();

        assertDoesNotThrow(() -> eventDao.deleteAllByIds(eventIds), "Delete was unsuccessful.");
    }

    private ZonedDateTime getEndDate(ZonedDateTime startDate, List<EventDto> events) {
        ZonedDateTime endDate;
        do {
            endDate = events.get(rnd.nextInt(1, events.size())).getEndDate();
        } while (endDate.isBefore(startDate));
        return endDate;
    }
}
