package com.tennisFriends.lesson;

import com.tennisFriends.account.CurrentUser;
import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Lesson;
import com.tennisFriends.lesson.form.LessonForm;
import com.tennisFriends.lesson.validator.LessonFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final ModelMapper modelMapper;
    private final LessonFormValidator lessonFormValidator;

    @InitBinder("lessonForm")
    public void lessonFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(lessonFormValidator);
    }

    @GetMapping("/new-lesson")
    public String newStudyForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new LessonForm());
        return "lesson/form";
    }

    @PostMapping("/new-lesson")
    public String newStudySubmit(@CurrentUser Account account, @Valid LessonForm lessonForm, Errors errors) {
        if (errors.hasErrors()) {
            return "lesson/form";
        }
        Lesson newLesson = lessonService.createNewLesson(modelMapper.map(lessonForm, Lesson.class), account);
        return "redirect:/lesson/" + URLEncoder.encode(newLesson.getPath(), StandardCharsets.UTF_8);
    }
}
