package com.tennisFriends.event;

import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Event;
import com.tennisFriends.domain.Lesson;
import com.tennisFriends.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(Event event, Lesson lesson, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setLesson(lesson);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
