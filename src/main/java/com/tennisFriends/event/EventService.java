package com.tennisFriends.event;

import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Event;
import com.tennisFriends.domain.Lesson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Lesson lesson, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setLesson(lesson);
        return eventRepository.save(event);
    }
}
