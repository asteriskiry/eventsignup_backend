/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.exception.ImageNotFoundException;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ImageServiceUnitTest {

    private ImageServiceImpl imageService;
    private final String rootPath = "/tmp";

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(rootPath);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBannerImage_giveFileDoesNotExist_expectImageNotFoundException() {
        assertThrows(ImageNotFoundException.class, () -> imageService.getBannerImage("not_existing"));
    }

    @Test
    void getBannerImage_givenFileExists_expectByteArray() throws IOException {
        var testFile = TestUtils.getImageDataAsBytes(rootPath);
        var result = imageService.getBannerImage("testFile.jpg");

        assertInstanceOf(byte[].class, result);
        assertEquals(testFile.length, result.length);
        assertArrayEquals(testFile, result);
    }

    @Test
    void getBannerImage_givenAFileThatCannotBeRead_expectImageNotFoundException() throws IOException {
        // Note: This test will run for several seconds!
        var fileName = createFileLargerThanMaxIntegerValue();

        assertThrows(ImageNotFoundException.class, () -> imageService.getBannerImage(fileName));
    }

    private String createFileLargerThanMaxIntegerValue() throws IOException {
        var outputFile = new File(rootPath + "/delete_me");
        var rnd = new Random();
        var bytes = new byte[100000];
        rnd.nextBytes(bytes);
        Files.write(outputFile.toPath(), bytes);
        int limit = (int) Math.round(Integer.MAX_VALUE / 100000.0) + 10;
        for (int i = 0; i < limit; i++) {
            rnd.nextBytes(bytes);
            Files.write(outputFile.toPath(), bytes, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        }
        outputFile.deleteOnExit();
        return outputFile.getName();
    }
}
