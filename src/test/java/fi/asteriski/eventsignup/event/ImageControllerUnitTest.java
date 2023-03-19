package fi.asteriski.eventsignup.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;

import static fi.asteriski.eventsignup.utils.TestUtils.getImageDataAsBytes;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageControllerUnitTest {

    @Value("${root.path.bannerimg}")
    private String rootPath;
    private ImageService imageService;
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        imageService = Mockito.mock(ImageService.class);
        imageController = new ImageController(imageService);
    }

    @Test
    @DisplayName("Get the path to a file that was saved.")
    void getBannerImagePath() {
        assertInstanceOf(Map.class, imageController.getBannerImagePath("123"));
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
