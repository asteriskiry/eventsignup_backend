/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.repo.archiving.ArchivedEventRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static fi.asteriski.eventsignup.utils.Constants.UTC_TIME_ZONE;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ArchivedEventDaoImplTest {
    @Autowired
    private ArchivedEventRepository archivedEventRepository;

    public ArchivedEventDao archivedEventDao;
    private final String testUser = "testUser";
    private final Supplier<Instant> lessThanHundredDaysAgoSupplier =
            () -> Instant.now().minus(60, ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
        archivedEventDao = new ArchivedEventDaoImpl(archivedEventRepository);
    }

    @AfterEach
    void tearDown() {
        archivedEventRepository.deleteAll();
    }

    @Test
    void save_givenSomethingToSave_expectNewIdInSavedObject() {
        var toArchive = TestUtils.createRandomArchivedEvent(testUser, Optional.empty());

        var result = archivedEventDao.save(toArchive);

        assertThat(result.id()).isNotNull();
        assertThat(toArchive.id()).isNotEqualTo(result.id());
    }

    @Test
    void saveAll_givenSomethingToSave_expectNewIdOnAllObjects() {
        var toArchive = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());

        var result = archivedEventDao.saveAll(toArchive);

        assertThat(result.stream().allMatch(event -> event.id() != null)).isTrue();
    }

    @Test
    void findAll_givenDatabaseHasItems_expectListOfItems() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());
        archivedEventRepository.saveAll(
                archivedEvents.stream().map(ArchivedEventDto::toEntity).toList());

        var result = archivedEventDao.findAll();

        assertThat(archivedEvents).hasSameSizeAs(result);
    }

    @Test
    void findAll_givenDatabaseHasNoItems_expectEmptyList() {
        var result = archivedEventDao.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByOriginalOwner_givenDatabaseHasArchivedEventsForMultipleUsers_expectListContainingOnlyTestUsers() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());
        var archivedEvents2 = TestUtils.getRandomArchivedEvents("otherTestUser", Optional.empty());
        archivedEventRepository.saveAll(Stream.concat(archivedEvents.stream(), archivedEvents2.stream())
                .map(ArchivedEventDto::toEntity)
                .toList());

        var result = archivedEventDao.findAllByOriginalOwner(testUser);

        assertThat(result).hasSameSizeAs(archivedEvents);
        assertThat(result.size()).isNotEqualTo((int) archivedEventRepository.count());
        assertThat(result.stream().allMatch(archivedEvent -> Objects.equals(archivedEvent.originalOwner(), testUser)))
                .isTrue();
    }

    @Test
    void findAllByOriginalOwner_givenDatabaseHasArchivedEventsForTestUser_expectListContainingOnlyTestUsers() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());
        archivedEventRepository.saveAll(
                archivedEvents.stream().map(ArchivedEventDto::toEntity).toList());

        var result = archivedEventDao.findAllByOriginalOwner(testUser);

        assertThat(result).hasSameSizeAs(archivedEvents).hasSize((int) archivedEventRepository.count());
        assertThat(result.stream().allMatch(archivedEvent -> Objects.equals(archivedEvent.originalOwner(), testUser)))
                .isTrue();
    }

    @Test
    void findAllByOriginalOwner_givenDatabaseHasArchivedEventsForOtherUsers_expectEmptyList() {
        var archivedEvents = TestUtils.getRandomArchivedEvents("otherTestUser", Optional.empty());
        archivedEventRepository.saveAll(
                archivedEvents.stream().map(ArchivedEventDto::toEntity).toList());

        var result = archivedEventDao.findAllByOriginalOwner(testUser);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteAllByDateArchivedIsBefore_givenRequiredDataExists_expectCountIsNotEqualToFullAmount() {
        var now = Instant.now();
        Supplier<Instant> moreThanHundredDaysAgoSupplier = () -> now.minus(120, ChronoUnit.DAYS);
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.of(moreThanHundredDaysAgoSupplier));
        archivedEvents.addAll(TestUtils.getRandomArchivedEvents(testUser, Optional.of(lessThanHundredDaysAgoSupplier)));
        var dateLimit = now.minus(100, ChronoUnit.DAYS);
        var numberOfEventsToRemove = archivedEvents.stream()
                .filter(archivedEventEntity ->
                        archivedEventEntity.dateArchived().isBefore(ZonedDateTime.ofInstant(dateLimit, UTC_TIME_ZONE)))
                .count();
        archivedEventRepository.saveAll(
                archivedEvents.stream().map(ArchivedEventDto::toEntity).toList());

        var countBefore = archivedEventRepository.count();
        archivedEventDao.deleteAllByDateArchivedIsBefore(dateLimit);
        var countAfter = archivedEventRepository.count();

        assertThat(countBefore).isNotEqualTo(countAfter);
        assertThat(countBefore - countAfter).isEqualTo(numberOfEventsToRemove);
    }

    @Test
    void deleteAllByDateArchivedIsBefore_givenThereAreNoneToRemove_expectNothingIsRemovedFromDatabase() {
        var now = Instant.now();
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.of(lessThanHundredDaysAgoSupplier));
        var dateLimit = now.minus(100, ChronoUnit.DAYS);
        var numberOfEventsToRemove = archivedEvents.stream()
                .filter(archivedEventEntity ->
                        archivedEventEntity.dateArchived().isBefore(ZonedDateTime.ofInstant(dateLimit, UTC_TIME_ZONE)))
                .count();
        archivedEventRepository.saveAll(
                archivedEvents.stream().map(ArchivedEventDto::toEntity).toList());

        var countBefore = archivedEventRepository.count();
        archivedEventDao.deleteAllByDateArchivedIsBefore(dateLimit);
        var countAfter = archivedEventRepository.count();

        assertThat(countBefore).isEqualTo(countAfter);
        assertThat(countBefore - countAfter).isEqualTo(numberOfEventsToRemove);
    }

    @Test
    void deleteById_givenIdExistsInDatabase_itIsDeletedFromDatabase() {
        var archivedEvent = TestUtils.createRandomArchivedEvent(testUser, Optional.empty());
        archivedEvent = archivedEventRepository.save(archivedEvent.toEntity()).toDto();

        archivedEventDao.deleteById(archivedEvent.id());

        assertThat(archivedEventRepository.existsById(archivedEvent.id())).isFalse();
    }
}
