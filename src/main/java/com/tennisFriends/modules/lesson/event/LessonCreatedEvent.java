package com.tennisFriends.modules.lesson.event;

import com.tennisFriends.modules.lesson.Lesson;
import lombok.Getter;

@Getter
public class LessonCreatedEvent {

    private Lesson lesson;
    public LessonCreatedEvent(Lesson lesson) {
        this.lesson = lesson;
    }
}
