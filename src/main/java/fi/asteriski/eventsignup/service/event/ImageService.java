/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.service.event;

public interface ImageService {
    byte[] getBannerImage(String fileName);

    String addBannerImage(byte[] file);

    String moveBannerImage(String originalPath);
}
