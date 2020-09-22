package me.studygroup.study.event;

import lombok.RequiredArgsConstructor;
import me.studygroup.study.donmain.Account;
import me.studygroup.study.donmain.Event;
import me.studygroup.study.donmain.Study;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Study study, Account account) {
            event.setCreatedBy(account);
            event.setCreatedDateTime(LocalDateTime.now());
            event.setStudy(study);
            return eventRepository.save(event);
    }
}
