/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.utils.Utils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

@Log4j2
@Service
public class ImageService {

    private static final String LOG_PREFIX = "[ImageService]";

    @Value("${root.path.bannerimg}")
    private String rootPath;

    public byte[] getBannerImage(String fileName) {
        File filePath = new File(String.format("%s/%s", rootPath, fileName));
        if (!filePath.canRead()) {
            log.info(String.format("%s Requested file %s doesn't exist and/or cannot be read.", LOG_PREFIX, fileName));
            throw new ImageNotFoundException(fileName.substring(fileName.lastIndexOf("/") + 1));
        }
        byte[] returnValue;
        try (InputStream inputStream = new FileInputStream(filePath.toString())) {
            returnValue = IOUtils.toByteArray(inputStream);
        } catch (IOException ioException) {
            log.error(String.format("%s IOError while reading file <%s>", LOG_PREFIX, fileName));
            throw new ImageNotFoundException(fileName.substring(fileName.lastIndexOf("/") + 1), ioException);
        }
        return returnValue;
    }

    public String addBannerImage(byte[] file, String userName) {
        if (isInputFileIsNonValidImage(file)) {
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
            finalFile = new File(String.format("%s/%s", targetDirectory, fileName));
        } while (finalFile.exists());
        // TODO check image size!
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(finalFile)) {
            imageOutputStream.write(file);
        } catch (IOException e) {
            log.info(String.format("IOException occurred :%s", e.getMessage()));
        }
        return String.format("%s_%s", userName, fileName);
    }

    private boolean isInputFileIsNonValidImage(byte[] inputFile) {
        BufferedImage final_buffered_image = null;
        try (ByteArrayInputStream input_stream= new ByteArrayInputStream(inputFile);) {
            final_buffered_image = ImageIO.read(input_stream);
        } catch (IOException ignored) {}
        return final_buffered_image == null;
    }
}
