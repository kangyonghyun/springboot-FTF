package com.tennisFriends.modules.lesson;

import com.tennisFriends.modules.account.Account;
import com.tennisFriends.modules.lesson.event.LessonCreatedEvent;
import com.tennisFriends.modules.tag.Tag;
import com.tennisFriends.modules.zone.Zone;
import com.tennisFriends.modules.lesson.form.LessonDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tennisFriends.modules.lesson.form.LessonForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Lesson createNewLesson(Lesson lesson, Account account) {
        Lesson newLesson = lessonRepository.save(lesson);
        newLesson.addManager(account);
        eventPublisher.publishEvent(new LessonCreatedEvent(newLesson));
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

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }
        return !lessonRepository.existsByPath(newPath);
    }

    public void updateLessonPath(Lesson lesson, String newPath) {
        lesson.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateLessonTitle(Lesson lesson, String newTitle) {
        lesson.setTitle(newTitle);
    }

    public void remove(Lesson lesson) {
        if (lesson.isRemovable()) {
            lessonRepository.delete(lesson);
        } else {
            throw new IllegalArgumentException("레슨을 삭제할 수 없습니다.");
        }
    }

    public void addMember(Lesson lesson, Account account) {
        lesson.addMember(account);
    }

    public void leaveMember(Lesson lesson, Account account) {
        lesson.leaveMember(account);
    }

    public Lesson getLessonToEnroll(String path) {
        Lesson lesson = lessonRepository.findLessonOnlyByPath(path);
        checkIfExistingLesson(path, lesson);
        return lesson;
    }
}
