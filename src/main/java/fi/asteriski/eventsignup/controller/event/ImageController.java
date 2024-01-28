/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.controller.event;

import fi.asteriski.eventsignup.model.event.BannerImageUploadSuccessResponse;
import fi.asteriski.eventsignup.service.event.ImageService;
import fi.asteriski.eventsignup.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@AllArgsConstructor
@RestController
@RequestMapping(Constants.API_PATH_EVENT)
public class ImageController {

    private ImageService imageService;

    @Operation(
            summary = "Get file path for the uploaded banner image.",
            parameters = {@Parameter(name = "fileName", description = "Filename generated when saving an image.")})
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Final path of the uploaded image file wrapped in json.",
                    content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = BannerImageUploadSuccessResponse.class))
                    }))
    @GetMapping("banner/{fileName}")
    public BannerImageUploadSuccessResponse getBannerImagePath(@PathVariable String fileName) {
        return BannerImageUploadSuccessResponse.builder().fileName(fileName).build();
    }

    @Operation(
            summary = "Get a banner image.",
            parameters = {@Parameter(name = "fileName", description = "File's name we want.")})
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "The file requested."),
                @ApiResponse(responseCode = "404", description = "File not found.")
            })
    @GetMapping(
            value = "banner/get/{fileName}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public byte[] getBannerImage(@PathVariable String fileName) {
        return imageService.getBannerImage(fileName.replace('_', '/'));
    }

    @Operation(
            summary = "Upload a new banner image.",
            parameters = {@Parameter(name = "file", description = "Raw bytes of the image being uploaded.")})
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "303", description = "Redirect to /api/event/banner/{fileName}."),
                @ApiResponse(responseCode = "401", description = "Unauthenticated"),
                @ApiResponse(responseCode = "406", description = "Invalid image file/file not an image."),
                @ApiResponse(responseCode = "500", description = "Target directory creation failed.")
            })
    @PostMapping("banner/add")
    public RedirectView addBannerImg(@RequestBody byte[] file) {
        String filePath = imageService.addBannerImage(file);
        return new RedirectView(String.format("/api/event/banner/%s", filePath));
    }
}
