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

    public Lesson getLesson(String path) {
        Lesson lesson = lessonRepository.findByPath(path);
        checkIfExistingLesson(path, lesson);
        return lesson;
    }

    private void checkIfExistingLesson(String path, Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException(path + "에 해당하는 레슨이 없습니다.");
        }
    }
}
