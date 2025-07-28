/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.controller.image;

import static fi.asteriski.eventsignup.supporting.utils.Constants.API_PATH_EVENT;

import fi.asteriski.eventsignup.components.service.ImageService;
import fi.asteriski.eventsignup.supporting.model.event.BannerImageUploadSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(API_PATH_EVENT)
public class ImageController {

    private ImageService imageService;

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
        return imageService.getBannerImage(fileName);
    }

    @Operation(
            summary = "Upload a new banner image.",
            parameters = {@Parameter(name = "file", description = "Raw bytes of the image being uploaded.")})
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Obfuscated file name and path.",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BannerImageUploadSuccessResponse.class))
                        }),
                @ApiResponse(responseCode = "401", description = "Unauthenticated"),
                @ApiResponse(responseCode = "406", description = "Invalid image file/file not an image."),
                @ApiResponse(responseCode = "500", description = "Target directory creation failed.")
            })
    @PostMapping("banner/add")
    public BannerImageUploadSuccessResponse addBannerImg(@RequestBody byte[] file) {
        var fileName = imageService.addBannerImage(file);
        return BannerImageUploadSuccessResponse.builder().fileName(fileName).build();
    }
}
