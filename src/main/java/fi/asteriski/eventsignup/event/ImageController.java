/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Get file path for the uploaded banner image.", parameters =
        {@Parameter(name = "fileName", description = "Filename generated when saving an image."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "Json: {'fileName': fileName}")
    )
    @GetMapping("/event/banner/{fileName}")
    @ResponseBody
    public Map<String, String> getBannerImagePath(@PathVariable String fileName, @AuthenticationPrincipal User loggedInUser) {
        Map<String, String> returnValue = new HashMap<>();
        returnValue.put("fileName", fileName);
        return returnValue;
    }

    @Operation(summary = "Get a banner image.", parameters = {
        @Parameter(name = "fileName", description = "File's name we want."),
        @Parameter(name = "loggedInUser", description = "Automatically added currently logged in user.")
    })
    @ApiResponses( value = {
        @ApiResponse(responseCode = "200", description = "The file requested."),
        @ApiResponse(responseCode = "404", description = "File not found.")
    }
    )
    @GetMapping(value = "/event/banner/get/{fileName}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public byte[] getBannerImage(@PathVariable String fileName, @AuthenticationPrincipal User loggedInUser) {
        return imageService.getBannerImage(fileName.replace('_', '/'));
    }

    @Operation(summary = "Upload a new banner image.", parameters =
        {@Parameter(name = "file", description = "Raw bytes of the image being uploaded."),
            @Parameter(name = "loggedInUser", description = "Not required. Automatically added currently logged in user.")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "303", description = "Redirect to /api/event/banner/{fileName}."),
        @ApiResponse(responseCode = "401", description = "Unauthenticated"),
        @ApiResponse(responseCode = "406", description = "Invalid image file/file not an image."),
        @ApiResponse(responseCode = "500", description = "Target directory creation failed.")
    })
    @PostMapping("/event/banner/add")
    public RedirectView addBannerImg(@RequestBody byte[] file, @AuthenticationPrincipal User loggedInUser) {
        // During testing loggedInUser will be null.
        var user = loggedInUser != null ? loggedInUser.getUsername() : "testUser";
        String filePath = imageService.addBannerImage(file, user);
        return new RedirectView("/event/banner/" + filePath);
    }
}
