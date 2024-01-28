/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.event;

import lombok.Builder;

@Builder
public record BannerImageUploadSuccessResponse(String fileName) {}
