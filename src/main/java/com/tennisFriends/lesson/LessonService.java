package com.tennisFriends.lesson;

import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Lesson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public Lesson createNewLesson(Lesson lesson, Account account) {
        Lesson newLesson = lessonRepository.save(lesson);
        newLesson.addManager(account);
        return newLesson;
    }
}
