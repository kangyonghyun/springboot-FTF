package com.tennisFriends.modules.lesson.validator;

import com.tennisFriends.modules.lesson.LessonRepository;
import com.tennisFriends.modules.lesson.form.LessonForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class LessonFormValidator implements Validator {

    private final LessonRepository lessonRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return LessonForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LessonForm lessonForm = (LessonForm) target;
        if (lessonRepository.existsByPath(lessonForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "레슨 경로값을 사용할 수 없습니다.");
        }
    }
}
