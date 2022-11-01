package com.tennisFriends.lesson;

import com.tennisFriends.account.CurrentUser;
import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Lesson;
import com.tennisFriends.lesson.form.LessonDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/lesson/{path}/settings")
@RequiredArgsConstructor
public class LessonSettingsController {

    private final LessonRepository lessonRepository;
    private final LessonService lessonService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewLessonSetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(lesson);
        model.addAttribute(modelMapper.map(lesson, LessonDescriptionForm.class));
        return "lesson/settings/description";
    }

    @PostMapping("/description")
    public String updateLessonInfo(@CurrentUser Account account, @PathVariable String path,
                                   @Valid LessonDescriptionForm lessonDescriptionForm, Errors errors,
                                   Model model, RedirectAttributes attributes) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(lesson);
            return "lesson/settings/description";
        }
        lessonService.updateLessonDescription(lesson, lessonDescriptionForm);
        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/lesson/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

}
