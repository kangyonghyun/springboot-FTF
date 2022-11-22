package com.tennisFriends.modules.lesson.event;

import com.tennisFriends.modules.lesson.Lesson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Transactional(readOnly = true)
@Component
public class LessonEventListener {

    @EventListener
    public void handleLessonCreatedEvent(LessonCreatedEvent lessonCreatedEvent) {
        Lesson lesson = lessonCreatedEvent.getLesson();
        log.info(lesson.getTitle() + " is created.");
        throw new RuntimeException();
    }

}
