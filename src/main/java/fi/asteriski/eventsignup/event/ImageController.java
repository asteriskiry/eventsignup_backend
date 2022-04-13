/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.User;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/event/banner/{fileName}")
    @ResponseBody
    public Map<String, String> getBannerImagePath(@PathVariable String fileName, @AuthenticationPrincipal User loggedInUser) {
        Map<String, String> returnValue = new HashMap<>();
        returnValue.put("fileName", fileName);
        return returnValue;
    }

    @GetMapping(value = "/event/banner/get/{fileName}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public byte[] getBannerImage(@PathVariable String fileName, @AuthenticationPrincipal User loggedInUser) {
        return imageService.getBannerImage(fileName.replace('_', '/'));
    }

    @PostMapping("/event/banner/add")
    public RedirectView addBannerImg(@RequestBody byte[] file, @AuthenticationPrincipal User loggedInUser) {
        // During testing loggedInUser will be null.
        var user = loggedInUser != null ? loggedInUser.getUsername() : "testUser";
        String filePath = imageService.addBannerImage(file, user);
        return new RedirectView("/event/banner/" + filePath);
    }
}
