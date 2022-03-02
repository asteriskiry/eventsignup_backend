/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import org.springframework.http.MediaType;
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

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/event/banner/{fileName}")
    @ResponseBody
    public Map<String, String> getBannerImagePath(@PathVariable String fileName) {
        Map<String, String> returnValue = new HashMap<>();
        returnValue.put("fileName", fileName);
        return returnValue;
    }

    @GetMapping(value = "/event/banner/get/{fileName}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public byte[] getBannerImage(@PathVariable String fileName) {
        return imageService.getBannerImage(fileName.replace('_', '/'));
    }

//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/event/banner/add")
    public RedirectView addBannerImg(@RequestBody byte[] file, Principal principal) {
        // FIXME principal can be null
        var user = principal != null ? principal.getName() : "user";
        String filePath = imageService.addBannerImage(file, user);
        return new RedirectView("/event/banner/" + filePath);
    }
}
