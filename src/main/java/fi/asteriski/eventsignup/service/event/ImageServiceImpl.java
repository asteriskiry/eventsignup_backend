/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.exception.FileMoveNotSuccessfulException;
import fi.asteriski.eventsignup.exception.ImageDirectoryCreationFailedException;
import fi.asteriski.eventsignup.exception.ImageNotFoundException;
import fi.asteriski.eventsignup.exception.InvalidImageFileException;
import fi.asteriski.eventsignup.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final String LOG_PREFIX = "[ImageServiceImpl]";
    private static final String FILE_PATH_TEMPLATE = "%s/%s";

    @Value("${root.path.bannerimg}")
    private String rootPath;

    @Override
    public byte[] getBannerImage(String fileName) {
        File filePath = new File(String.format(FILE_PATH_TEMPLATE, rootPath, fileName));
        if (!filePath.canRead()) {
            log.info(String.format("%s Requested file %s doesn't exist and/or cannot be read.", LOG_PREFIX, fileName));
            throw new ImageNotFoundException(fileName.substring(fileName.lastIndexOf("/") + 1));
        }
        byte[] returnValue;
        try (InputStream inputStream = new FileInputStream(filePath.toString())) {
            returnValue = IOUtils.toByteArray(inputStream);
        } catch (IOException | IllegalArgumentException ioException) {
            log.error(String.format("%s IOError while reading file <%s>", LOG_PREFIX, fileName));
            throw new ImageNotFoundException(fileName.substring(fileName.lastIndexOf("/") + 1), ioException);
        }
        return returnValue;
    }

    @Override
    public String addBannerImage(byte[] file) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userName = authentication.getName();
        if (isInputFileNonValidImage(file)) {
            log.info(String.format("%s %s", LOG_PREFIX, "Input file is not a valid image. Throwing exception."));
            throw new InvalidImageFileException("Provided file is not a valid image file.");
        }
        File targetDirectory = new File(String.format("%s/%s/", rootPath, userName));
        if (!targetDirectory.canRead() && !targetDirectory.isDirectory()) {
            try {
                Files.createDirectories(targetDirectory.toPath());
            } catch (IOException ioException) {
                var errorMessage = String.format("Target directory <%s> creation failed.", targetDirectory);
                log.info(String.format("%s Throwing exception.", errorMessage));
                throw new ImageDirectoryCreationFailedException(errorMessage, ioException);
            }
        }
        String fileName;
        File finalFile;
        do {
            fileName = Utils.generateRandomString(30);
            finalFile = new File(String.format(FILE_PATH_TEMPLATE, targetDirectory, fileName));
        } while (finalFile.exists());
        // TODO check image size!
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(finalFile)) {
            imageOutputStream.write(file);
        } catch (IOException e) {
            log.info(String.format("IOException occurred: %s", e.getMessage()));
        }
        return String.format("%s_%s", userName, fileName);
    }

    @Override
    public String moveBannerImage(final String originalPath) {
        var archivedDirectory = new File(String.format("%s/archived/", rootPath));
        var targetDirectory = String.format("%s/archived/%s", rootPath, originalPath.replace("_", "/"));
        var sourceDirectory = String.format(FILE_PATH_TEMPLATE, rootPath, originalPath.replace("_", "/"));
        if (!archivedDirectory.exists() && archivedDirectory.isDirectory()) {
            try {
                Files.createDirectories(archivedDirectory.toPath());
            } catch (IOException e) {
                var errorMessage = String.format("Target directory <%s> creation failed.", archivedDirectory);
                log.error(errorMessage);
                throw new ImageDirectoryCreationFailedException(errorMessage, e);
            }
        }
        try {
            Files.move(Path.of(sourceDirectory), Path.of(targetDirectory));
        } catch (IOException e) {
            var errorMessage = String.format("Failed to move banner image <%s> to archive.", originalPath.replace("_", "/"));
            log.error(errorMessage);
            throw new FileMoveNotSuccessfulException(errorMessage, e);
        }

        return targetDirectory;
    }

    private boolean isInputFileNonValidImage(byte[] inputFile) {
        BufferedImage finalBufferedImage = null;
        try (ByteArrayInputStream inputStream= new ByteArrayInputStream(inputFile)) {
            finalBufferedImage = ImageIO.read(inputStream);
        } catch (IOException ignored) {}
        return finalBufferedImage == null;
    }
}
