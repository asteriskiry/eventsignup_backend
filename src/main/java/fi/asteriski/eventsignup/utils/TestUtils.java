/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Form;
import fi.asteriski.eventsignup.domain.Participant;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class for utility methods used in testing.
 */
public final class TestUtils {

    // To prevent instantiation of the class.
    private TestUtils(){}

    public static Event createRandomEvent() {
        var random = new Random();
        var form = new Form();
        var instant = random.nextBoolean() ?  Instant.now().minus(random.nextLong(10, 100), ChronoUnit.DAYS) : Instant.now().plus(random.nextLong(10, 100), ChronoUnit.DAYS);
        return new Event(
            Utils.generateRandomString(random.nextInt(5, 15)),
            instant,
            Utils.generateRandomString(random.nextInt(5, 15)),
            Utils.generateRandomString(random.nextInt(20, 50)),
            form
        );
    }

    public static List<Event> getRandomEvents() {
        List<Event> returnValue = new ArrayList<>();
        var random = new Random();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomEvent());
        }
        return returnValue;
    }

    public static List<Participant> getRandomParticipants() {
        List<Participant> returnValue = new ArrayList<>();
        var random = new Random();
        for (int i = 0; i < random.nextInt(10, 101); i++) {
            returnValue.add(createRandomParticipant());
        }
        return returnValue;
    }

    public static Participant createRandomParticipant() {
        var random = new Random();
        return new Participant(
            Utils.generateRandomString(random.nextInt(5, 15)),
            Utils.generateRandomString(random.nextInt(5, 15)),
            Utils.generateRandomString(random.nextInt(5, 15))
        );
    }

    /**
     * <p>Copies test file to a location expected by the application and returns it as byte[].<br>
     * Copied file is set to delete on exit.</p>
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
        var finalFile = new File(rootPath+"/testFile.jpg");
        try (InputStream inputStream = new FileInputStream(finalFile.toString())) {
            file = IOUtils.toByteArray(inputStream);
        } catch (IOException ignored) {}
        finalFile.deleteOnExit();
        return file;
    }
}
