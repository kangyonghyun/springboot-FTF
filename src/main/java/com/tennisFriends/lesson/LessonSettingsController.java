package com.tennisFriends.lesson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisFriends.account.CurrentUser;
import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Lesson;
import com.tennisFriends.domain.Tag;
import com.tennisFriends.lesson.form.LessonDescriptionForm;
import com.tennisFriends.settings.form.TagForm;
import com.tennisFriends.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lesson/{path}/settings")
@RequiredArgsConstructor
public class LessonSettingsController {

    private final LessonRepository lessonRepository;
    private final LessonService lessonService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final TagRepository tagRepository;

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

    @GetMapping("/banner")
    public String lessonImageForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(lesson);
        return "lesson/settings/banner";
    }

    @PostMapping("/banner")
    public String lessonImageSubmit(@CurrentUser Account account, @PathVariable String path, String image, RedirectAttributes attributes) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        lessonService.updateLessonImage(lesson, image);
        attributes.addFlashAttribute("message", "레슨 이미지를 수정했습니다.");
        return "redirect:/lesson/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableLessonBanner(@CurrentUser Account account, @PathVariable String path) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        lessonService.enableLessonBanner(lesson);
        return "redirect:/lesson/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableLessonBanner(@CurrentUser Account account, @PathVariable String path) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        lessonService.disableLessonBanner(lesson);
        return "redirect:/lesson/" + getPath(path) + "/settings/banner";
    }

    @GetMapping("/tags")
    public String lessonTagsForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        model.addAttribute(lesson);
        model.addAttribute(account);
        model.addAttribute("tags", lesson.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "lesson/settings/tags";
    }

    @PostMapping("/tags/add")
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Lesson lesson = lessonService.getLessonToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle()).orElseGet(() -> tagRepository.save(Tag.builder()
                .title(tagForm.getTagTitle()).build()));
        lessonService.addTag(lesson, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Lesson lesson = lessonService.getLessonToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle()).orElse(null);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        lessonService.removeTag(lesson, tag);
        return ResponseEntity.ok().build();
    }

}
