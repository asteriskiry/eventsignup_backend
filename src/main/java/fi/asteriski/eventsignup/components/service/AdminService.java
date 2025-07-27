package fi.asteriski.eventsignup.components.service;

import fi.asteriski.eventsignup.components.dao.repository.EventRepository;
import fi.asteriski.eventsignup.components.dao.repository.FormRepository;
import fi.asteriski.eventsignup.components.dao.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {

    @Autowired private FormRepository formRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private ParticipantRepository participantRepository;

    //Shouldn't do code like this, should go through the service layer,
        //not accessing repository directly. Better would be participantservice.deleteAll
        //but hopefully this code will be deleted for prod, it's volatile anyway
    @Transactional
    public void wipeDatabase() {
        participantRepository.deleteAll();
        formRepository.deleteAll();
        eventRepository.deleteAll();
    }
}
