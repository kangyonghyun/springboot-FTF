package com.tennisFriends.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisFriends.WithAccount;
import com.tennisFriends.account.AccountRepository;
import com.tennisFriends.account.AccountService;
import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Tag;
import com.tennisFriends.settings.form.TagForm;
import com.tennisFriends.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    AccountService accountService;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("yong")
    @Test
    @DisplayName("프로필 수정 폼")
    void updateProfile_form() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("프로필 수정하기 - 입력값 정상")
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));
        Account yong = accountRepository.findByNickname("yong");
        assertThat(yong.getBio()).isEqualTo(bio);
    }

    @WithAccount("yong")
    @Test
    @DisplayName("프로필 수정하기 - 입력값 에러")
    void updateProfile_error() throws Exception {
        String bio = "짧은 소개를 수정하는 경우, 길게 소개를 수정하는 경우, 너무 길게 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_MAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());
        Account yong = accountRepository.findByNickname("yong");
        assertThat(yong.getBio()).isNull();
    }

    @WithAccount("yong")
    @Test
    @DisplayName("패스워드 변경하기 폼")
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("패스워드 변경하기 - 입력값 정상")
    void updatePassword() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account yong = accountRepository.findByNickname("yong");
        assertThat(passwordEncoder.matches("12345678", yong.getPassword())).isTrue();
    }

    @WithAccount("yong")
    @Test
    @DisplayName("패스워드 변경하기 - 입력값 에러")
    void updatePassword_error() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "11111111")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("닉네임 변경 폼")
    void updateNickname_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("닉네임 변경하기 - 입력값 정상")
    void updateNickname() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .with(csrf())
                        .param("nickname", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("yong")
    @ParameterizedTest
    @ValueSource(strings = {"as", "#asd", "asd%", "123456789123456789111"})
    @DisplayName("닉네임 변경하기 - 입력값 에러")
    void updateNickname_error(String nickname) throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("nickname", nickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("태그 수정 폼")
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("계정에 태그 추가")
    void addTags() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag").get();
        assertThat(newTag).isNotNull();
        Set<Tag> tags = accountRepository.findByNickname("yong").getTags();
        assertThat(tags).contains(newTag);
    }

    @WithAccount("yong")
    @Test
    @DisplayName("계정에 태그 삭제")
    void removeTags() throws Exception {
        Account yong = accountRepository.findByNickname("yong");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(yong, newTag);

        assertThat(yong.getTags()).contains(newTag);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        accountService.removeTag(yong, newTag);

        assertThat(yong.getTags()).doesNotContain(newTag);
    }

}