package com.tennisFriends.modules.lesson;

import com.tennisFriends.infra.AbstractContainerBaseTest;
import com.tennisFriends.infra.MockMvcTest;
import com.tennisFriends.modules.account.Account;
import com.tennisFriends.modules.account.AccountRepository;
import com.tennisFriends.modules.account.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class LessonControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    LessonService lessonService;
    @Autowired
    LessonRepository lessonRepository;
    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("yong")
    @DisplayName("레슨 개설 폼 조회")
    void createLessonForm() throws Exception {
        mockMvc.perform(get("/new-lesson"))
                .andExpect(status().isOk())
                .andExpect(view().name("lesson/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("lessonForm"));
    }

    @Test
    @WithAccount("yong")
    @DisplayName("레슨 개설 - 완료")
    void createLesson_success() throws Exception {
        mockMvc.perform(post("/new-lesson")
                        .param("path", "test-path")
                        .param("title", "lesson title")
                        .param("shortDescription", "short description of a lesson")
                        .param("fullDescription", "full description of a lesson")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lesson/test-path"));
        Lesson lesson = lessonRepository.findByPath("test-path");
        assertThat(lesson).isNotNull();
        Account yong = accountRepository.findByNickname("yong");
        assertThat(lesson.getManagers()).contains(yong);
    }

    @Test
    @WithAccount("yong")
    @DisplayName("레슨 개설 - 실패")
    void createLesson_fail() throws Exception {
        mockMvc.perform(post("/new-lesson")
                        .param("path", "wrong path")
                        .param("title", "lesson title")
                        .param("shortDescription", "short description of a lesson")
                        .param("fullDescription", "full description of a lesson")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("lesson/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("lessonForm"))
                .andExpect(model().attributeExists("account"));

        Lesson lesson = lessonRepository.findByPath("test-path");
        assertThat(lesson).isNull();
    }

    @Test
    @WithAccount("yong")
    @DisplayName("레슨 조회")
    void viewLesson() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setPath("test-path");
        lesson.setTitle("test-lesson");
        lesson.setShortDescription("lesson short");
        lesson.setFullDescription("lesson full");

        Account yong = accountRepository.findByNickname("yong");
        lessonService.createNewLesson(lesson, yong);

        mockMvc.perform(get("/lesson/test-path"))
                .andExpect(view().name("lesson/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("lesson"));
    }
}