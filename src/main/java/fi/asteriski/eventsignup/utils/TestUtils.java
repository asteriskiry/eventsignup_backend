/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.event.Form;
import fi.asteriski.eventsignup.domain.signup.Participant;
import org.apache.commons.io.IOUtils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import static fi.asteriski.eventsignup.Constants.UTC_TIME_ZONE;

/**
 * Class for utility methods used in testing.
 */
public final class TestUtils {

    private static final Random random = new Random();
    private static final Supplier<Instant> defaultDateArchivedSupplier = () -> Instant.now().minus(random.nextInt(10, 400), ChronoUnit.DAYS);

    // To prevent instantiation of the class.
    private TestUtils(){}

    public static EventDto createRandomEvent(String owner) {
        var form = new Form();
        var instant = random.nextBoolean() ? ZonedDateTime.now().minusDays(random.nextLong(10, 100)) : ZonedDateTime.now().plusDays(random.nextLong(10, 100));
        var event = EventDto.builder()
            .name(Utils.generateRandomString(random.nextInt(5, 15)))
            .startDate(instant)
            .endDate(instant.plusDays(random.nextLong(10, 100)))
            .place(Utils.generateRandomString(random.nextInt(5, 15)))
            .description(Utils.generateRandomString(random.nextInt(20, 50)))
            .form(form)
            .build();
        if (owner != null) {
            event.setOwner(owner);
        }
        return event;
    }

    public static List<EventDto> getRandomEvents(String owner) {
        var returnValue = new ArrayList<EventDto>();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomEvent(owner));
        }
        return returnValue;
    }

    public static List<Participant> getRandomParticipants(String eventId) {
        var returnValue = new ArrayList<Participant>();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomParticipant(eventId));
        }
        return returnValue;
    }

    public static Participant createRandomParticipant(String eventId) {
        eventId = eventId != null ? eventId : Utils.generateRandomString(random.nextInt(5, 15));
        return new Participant(
            Utils.generateRandomString(random.nextInt(5, 15)),
            Utils.generateRandomString(random.nextInt(5, 15)),
            eventId
        );
    }

    /**
     * <p>Copies test file to a location expected by the application and returns it as byte[].<br>
     * Copied file is set to delete on exit.</p>
     *
     * @return Byte[] of the test file.
     * @throws IOException If I/O error occurs when copying the file.
     */
    public static byte[] getImageDataAsBytes(String rootPath) throws IOException {
        byte[] file = new byte[20];
        if (rootPath == null) {
            random.nextBytes(file);
            return file;
        }
        Files.copy(Path.of("./testData/testFile.jpg"), Path.of(rootPath + "/testFile.jpg"), StandardCopyOption.REPLACE_EXISTING);
        var finalFile = new File(rootPath + "/testFile.jpg");
        try (InputStream inputStream = new FileInputStream(finalFile.toString())) {
            file = IOUtils.toByteArray(inputStream);
        } catch (IOException ignored) {
        }
        finalFile.deleteOnExit();
        return file;
    }

    public static List<ArchivedEventDto> getRandomArchivedEvents(@NotNull String owner, Optional<Supplier<Instant>> dateArchivedSupplier) {
        var events = new ArrayList<ArchivedEventDto>();
        for (int i = 0; i < random.nextInt(200, 1001); i++) {
            events.add(createRandomArchivedEvent(owner, dateArchivedSupplier));
        }
        return events;
    }

    public static ArchivedEventDto createRandomArchivedEvent(@NotNull String owner, Optional<Supplier<Instant>> dateArchivedSupplier) {
        var event = createRandomEvent(owner);
        var dateArchived = dateArchivedSupplier.orElse(defaultDateArchivedSupplier).get();
        return ArchivedEventDto.builder()
            .originalEvent(event)
            .dateArchived(ZonedDateTime.ofInstant(dateArchived, UTC_TIME_ZONE))
            .numberOfParticipants(random.nextLong(20,200))
            .originalOwner(owner)
            .bannerImage("test_test")
            .build();
    }

    public static List<String> createRandomIdList() {
        return random.ints()
            .limit(random.nextLong(20, 200))
            .mapToObj(i -> Utils.generateRandomString(10))
            .toList();
    }
}
