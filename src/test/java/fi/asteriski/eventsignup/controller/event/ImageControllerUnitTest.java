package fi.asteriski.eventsignup.controller.event;

import fi.asteriski.eventsignup.exception.ImageNotFoundException;
import fi.asteriski.eventsignup.model.event.BannerImageUploadSuccessResponse;
import fi.asteriski.eventsignup.service.event.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

import static fi.asteriski.eventsignup.utils.TestUtils.getImageDataAsBytes;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageControllerUnitTest {

    @Value("${root.path.bannerimg}")
    private String rootPath;
    private ImageServiceImpl imageService;
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        imageService = Mockito.mock(ImageServiceImpl.class);
        imageController = new ImageController(imageService);
    }

    @Test
    @DisplayName("Get the path to a file that was saved.")
    void getBannerImagePath() {
        var instance = imageController.getBannerImagePath("123");
        assertInstanceOf(BannerImageUploadSuccessResponse.class, instance);
        assertEquals("123", instance.fileName());
    }

    @Test
    @DisplayName("Get an existing banner image file (i.e. byte array).")
    void getBannerImageThatExists() {
        when(imageService.getBannerImage("123")).thenReturn(new byte[10]);
        assertInstanceOf(byte[].class, imageController.getBannerImage("123"));
    }

    @Test
    @DisplayName("Try to get a non existent banner image file.")
    void getNonExistentBannerImage() {
        when(imageService.getBannerImage("not/exist")).thenThrow(new ImageNotFoundException("not found"));
        assertThrows(ImageNotFoundException.class, () -> imageController.getBannerImage("not_exist"));
        verify(imageService).getBannerImage("not/exist");
    }

    @Test
    @DisplayName("Add an image (i.e. a byte array).")
    void addBannerImg() throws IOException {
        byte[] bytes = getImageDataAsBytes(rootPath);
        when(imageService.addBannerImage(bytes)).thenReturn("value");
        assertInstanceOf(RedirectView.class, imageController.addBannerImg(bytes));
        verify(imageService).addBannerImage(bytes);
    }
}
