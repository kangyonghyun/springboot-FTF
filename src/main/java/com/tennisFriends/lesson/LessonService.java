package com.tennisFriends.lesson;

import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Lesson;
import com.tennisFriends.domain.Tag;
import com.tennisFriends.domain.Zone;
import com.tennisFriends.lesson.form.LessonDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;

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

    public Lesson getLessonToUpdate(Account account, String path) {
        Lesson lesson = getLesson(path);
        checkIfManager(lesson, account);
        return lesson;
    }

    private void checkIfManager(Lesson lesson, Account account) {
        if (!lesson.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void updateLessonDescription(Lesson lesson, LessonDescriptionForm lessonDescriptionForm) {
        modelMapper.map(lessonDescriptionForm, lesson);
    }

    public void updateLessonImage(Lesson lesson, String image) {
        lesson.setImage(image);
    }

    public void enableLessonBanner(Lesson lesson) {
        lesson.setUseBanner(true);
    }

    public void disableLessonBanner(Lesson lesson) {
        lesson.setUseBanner(false);
    }

    public Lesson getLessonToUpdateTag(Account account, String path) {
        Lesson lesson = lessonRepository.findLessonWithTagsByPath(path);
        checkIfExistingLesson(path, lesson);
        checkIfManager(lesson, account);
        return lesson;
    }

    public void addTag(Lesson lesson, Tag tag) {
        lesson.getTags().add(tag);
    }

    public void removeTag(Lesson lesson, Tag tag) {
        lesson.getTags().remove(tag);
    }

    public Lesson getLessonToUpdateZone(Account account, String path) {
        Lesson lesson = lessonRepository.findLessonWithZonesByPath(path);
        checkIfExistingLesson(path, lesson);
        checkIfManager(lesson, account);
        return lesson;
    }

    public void addZone(Lesson lesson, Zone zone) {
        lesson.getZones().add(zone);
    }

    public void removeZone(Lesson lesson, Zone zone) {
        lesson.getZones().remove(zone);
    }

    public Lesson getLessonToUpdateStatus(Account account, String path) {
        Lesson lesson = lessonRepository.findLessonWithManagersByPath(path);
        checkIfExistingLesson(path, lesson);
        checkIfManager(lesson, account);
        return lesson;
    }

    public void publish(Lesson lesson) {
        lesson.publish();
    }

    public void close(Lesson lesson) {
        lesson.close();
    }

    public void startRecruit(Lesson lesson) {
        lesson.startRecruit();
    }

    public void stopRecruit(Lesson lesson) {
        lesson.stopRecruit();
    }
}
