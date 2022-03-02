package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;

@Data
public class Event {

    @Id
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private Instant startDate;
    @NonNull
    private String place;
    @NonNull
    private String description;
    @NonNull
    private Form form;
    private Instant endDate;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Instant signupStarts;
    private Instant signupEnds;
    private Map<String, Object> quotas;
    private String bannerImg;
    private Map<String, Object> otherData;
    private Map<String, Object> metaData;

//    Event(Event eventToCopy) {
//        this.name = eventToCopy.name;
//        this.startDate = eventToCopy.startDate;
//        this.place = eventToCopy.place;
//        this.description = eventToCopy.description;
//        this.form = eventToCopy.form; // FIXME do i need this?
//        this.endDate = eventToCopy.endDate;
//        this.minParticipants = eventToCopy.minParticipants;
//        this.maxParticipants = eventToCopy.maxParticipants;
//        this.signupStarts = eventToCopy.signupStarts;
//        this.signupEnds = eventToCopy.signupEnds;
//        this.quotas = eventToCopy.quotas;
//        this.bannerImg = eventToCopy.bannerImg;
//        this.otherData = eventToCopy.otherData;
//        this.metaData = eventToCopy.metaData;
//    }
}
