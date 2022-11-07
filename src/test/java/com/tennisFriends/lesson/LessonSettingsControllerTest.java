package com.tennisFriends.lesson;

import com.tennisFriends.WithAccount;
import com.tennisFriends.account.AccountRepository;
import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Lesson;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class LessonSettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LessonService lessonService;

    @Test
    @WithAccount("young")
    @DisplayName("레슨 소개 수정 폼 조회 - 성공")
    void updateDescriptionForm_success() throws Exception {
        Account young = accountRepository.findByNickname("young");
        Lesson lesson = new Lesson();
        lesson.setPath("test-path");
        lesson.setTitle("test-lesson");
        lesson.setShortDescription("lesson short");
        lesson.setFullDescription("lesson full");
        lessonService.createNewLesson(lesson, young);

        mockMvc.perform(get("/lesson/" + lesson.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("lesson/settings/description"))
                .andExpect(model().attributeExists("lessonDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("lesson"));
    }

    @Test
    @WithAccount("young")
    @DisplayName("레슨 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account young = accountRepository.findByNickname("young");
        Lesson lesson = new Lesson();
        lesson.setPath("test-path");
        lesson.setTitle("test-lesson");
        lesson.setShortDescription("lesson short");
        lesson.setFullDescription("lesson full");
        lessonService.createNewLesson(lesson, young);

        String settingsDescriptionUrl = "/lesson/" + lesson.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("young")
    @DisplayName("레슨 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account young = accountRepository.findByNickname("young");
        Lesson lesson = new Lesson();
        lesson.setPath("test-path");
        lesson.setTitle("test-lesson");
        lesson.setShortDescription("lesson short");
        lesson.setFullDescription("lesson full");
        lessonService.createNewLesson(lesson, young);

        String settingsDescriptionUrl = "/lesson/" + lesson.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                        .param("shortDescription", "")
                        .param("fullDescription", "lesson full")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("lessonDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("lesson"));
    }


}