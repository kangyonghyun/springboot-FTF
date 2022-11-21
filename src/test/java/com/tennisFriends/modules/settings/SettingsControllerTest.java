package com.tennisFriends.modules.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisFriends.WithAccount;
import com.tennisFriends.modules.account.AccountRepository;
import com.tennisFriends.modules.account.AccountService;
import com.tennisFriends.modules.account.Account;
import com.tennisFriends.modules.tag.Tag;
import com.tennisFriends.modules.zone.Zone;
import com.tennisFriends.modules.settings.form.TagForm;
import com.tennisFriends.modules.settings.form.ZoneForm;
import com.tennisFriends.modules.tag.TagRepository;
import com.tennisFriends.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static com.tennisFriends.modules.settings.SettingsController.*;
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
    @Autowired
    ZoneRepository zoneRepository;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithAccount("yong")
    @Test
    @DisplayName("프로필 수정 폼")
    void updateProfile_form() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("프로필 수정하기 - 입력값 정상")
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PROFILE))
                .andExpect(flash().attributeExists("message"));
        Account yong = accountRepository.findByNickname("yong");
        assertThat(yong.getBio()).isEqualTo(bio);
    }

    @WithAccount("yong")
    @Test
    @DisplayName("프로필 수정하기 - 입력값 에러")
    void updateProfile_error() throws Exception {
        String bio = "짧은 소개를 수정하는 경우, 길게 소개를 수정하는 경우, 너무 길게 소개를 수정하는 경우";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
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
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("패스워드 변경하기 - 입력값 정상")
    void updatePassword() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account yong = accountRepository.findByNickname("yong");
        assertThat(passwordEncoder.matches("12345678", yong.getPassword())).isTrue();
    }

    @WithAccount("yong")
    @Test
    @DisplayName("패스워드 변경하기 - 입력값 에러")
    void updatePassword_error() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "11111111")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("닉네임 변경 폼")
    void updateNickname_form() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("닉네임 변경하기 - 입력값 정상")
    void updateNickname() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .with(csrf())
                        .param("nickname", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("yong")
    @ParameterizedTest
    @ValueSource(strings = {"as", "#asd", "asd%", "123456789123456789111"})
    @DisplayName("닉네임 변경하기 - 입력값 에러")
    void updateNickname_error(String nickname) throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", nickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("태그 수정 폼")
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(view().name(SETTINGS + TAGS))
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

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
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

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(yong.getTags()).doesNotContain(newTag);
    }

    @WithAccount("yong")
    @Test
    @DisplayName("지역활동 폼")
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @WithAccount("yong")
    @Test
    @DisplayName("계정에 지역활동 추가")
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertThat(zone).isNotNull();
        Set<Zone> zones = accountRepository.findByNickname("yong").getZones();
        assertThat(zones).contains(zone);
    }

    @WithAccount("yong")
    @Test
    @DisplayName("계정에 지역활동 삭제")
    void removeZone() throws Exception {
        Account yong = accountRepository.findByNickname("yong");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(yong, zone);

        assertThat(yong.getZones()).contains(zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(yong.getZones()).doesNotContain(zone);
    }

}