/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.*;
import org.apache.commons.io.IOUtils;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class for utility methods used in testing.
 */
public final class TestUtils {

    // To prevent instantiation of the class.
    private TestUtils(){}

    public static Event createRandomEvent(String owner) {
        var random = new Random();
        var form = new Form();
        var instant = random.nextBoolean() ? ZonedDateTime.now().minus(random.nextLong(10, 100), ChronoUnit.DAYS) : ZonedDateTime.now().plus(random.nextLong(10, 100), ChronoUnit.DAYS);
        var event = Event.builder()
            .name(Utils.generateRandomString(random.nextInt(5, 15)))
            .startDate(instant)
            .place(Utils.generateRandomString(random.nextInt(5, 15)))
            .description(Utils.generateRandomString(random.nextInt(20, 50)))
            .form(form)
            .build();
        if (owner != null) {
            event.setOwner(owner);
        }
        return event;
    }

    public static List<Event> getRandomEvents(String owner) {
        List<Event> returnValue = new LinkedList<>();
        var random = new Random();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomEvent(owner));
        }
        return returnValue;
    }

    public static List<Participant> getRandomParticipants(String eventId) {
        List<Participant> returnValue = new LinkedList<>();
        var random = new Random();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomParticipant(eventId));
        }
        return returnValue;
    }

    public static Participant createRandomParticipant(String eventId) {
        var random = new Random();
        eventId = eventId != null ? eventId : Utils.generateRandomString(random.nextInt(5, 15));
        return new Participant(
            Utils.generateRandomString(random.nextInt(5, 15)),
            Utils.generateRandomString(random.nextInt(5, 15)),
            eventId
        );
    }

    public static User createRandomUser(String username) {
        var rnd = new Random();
        return User.builder()
            .firstName(generateRandomString(10))
            .lastName(generateRandomString(10))
            .email("test@example.com")
            .userRole(UserRole.ROLE_USER)
            .enabled(true)
            .username(username)
            .password(generateRandomString(15))
            .expirationDate(Instant.now().plus(rnd.nextInt(50) + 1, ChronoUnit.DAYS))
            .build();
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
            new Random().nextBytes(file);
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

    public static String generateRandomString(int targetStringLength) {
        return Utils.generateRandomString(targetStringLength);
    }

    public static List<User> createListOfRandomUsers(String username) {
        List<User> returnValue = new LinkedList<>();
        var random = new Random();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomUser(username));
        }
        return returnValue;
    }
}
