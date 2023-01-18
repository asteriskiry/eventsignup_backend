/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
@EqualsAndHashCode
public class SignupEvent {

    private final String id;
    private final String name;
    private final ZonedDateTime startDate;
    private final String place;
    private final String description;
    private final Form form;
    private final ZonedDateTime endDate;
    private final Double price;
    private final String bannerImg;

    public SignupEvent(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.place = event.getPlace();
        this.form = event.getForm();
        this.price = event.getPrice();
        this.bannerImg = event.getBannerImg();
    }
}
