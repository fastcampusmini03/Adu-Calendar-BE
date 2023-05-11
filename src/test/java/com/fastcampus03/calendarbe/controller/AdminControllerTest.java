package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.MyRestDoc;
import com.fastcampus03.calendarbe.core.dummy.DummyEntity;
import com.fastcampus03.calendarbe.core.util.StatusConst;
import com.fastcampus03.calendarbe.dto.admin.AdminRequest;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("관리자 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnnualDutyRepository annualDutyRepository;
    @Autowired
    private UpdateRequestLogRepository updateRequestLogRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(dummy.newUser("ssar", "USER"));
        User admin = userRepository.save(dummy.newUser("admin", "ADMIN"));
    }

    @DisplayName("유저 권한 변경 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_role_test() throws Exception {
        // given
        User user = userRepository.findByEmail("ssar@nate.com").get();

        AdminRequest.UpdateRoleDTO updateRoleDTO = new AdminRequest.UpdateRoleDTO();
        updateRoleDTO.setEmail(user.getEmail());
        updateRoleDTO.setRole("ADMIN");
        String requestBody = om.writeValueAsString(updateRoleDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/role")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 등록 승인 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_save_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/accept/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.id").value(annualDutyPS.getId()))
                .andExpect(jsonPath("$.data.status").value(StatusConst.ACCEPTED))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 등록 승인 - 인증 실패")
    @Test
    public void accept_save_auth_fail_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/accept/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 등록 승인 - 유효한 토큰은 있으나 삭제된 유저일 경우")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_save_non_exist_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));
        User admin = userRepository.findByEmail("admin@nate.com").get();
        userRepository.delete(admin);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/accept/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("등록되지 않은 유저입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 등록 거절 - 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_reject_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/reject/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print()).andDo(document);
        Assertions.assertThat(annualDutyPS.getStatus()).isEqualTo(StatusConst.REJECTED);
    }

    @DisplayName("일정 등록 거절 - 인증 실패")
    @Test
    public void accept_reject_auth_fail_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/reject/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 등록 거절 - 유효한 토큰은 있으나 삭제된 유저일 경우")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_reject_non_exist_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));
        User admin = userRepository.findByEmail("admin@nate.com").get();
        userRepository.delete(admin);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/reject/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("등록되지 않은 유저입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 승인 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_delete_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(AnnualDuty.builder()
                        .user(taeheoki)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusDays(5))
                        .status(StatusConst.ACCEPTED)
                        .type(true)
                        .title(taeheoki.getUsername() + "의 일정")
                        .updateStatus(StatusConst.UPDATE_DELETESTATUS)
                        .build());

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/accept/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print()).andDo(document);
        Assertions.assertThat(annualDutyPS.getStatus()).isEqualTo(StatusConst.REJECTED);
        Assertions.assertThat(annualDutyPS.getUpdateStatus()).isEqualTo(StatusConst.UPDATE_DEFAULTSTATUS);
    }

    @DisplayName("일정 삭제 승인 - 인증 실패")
    @Test
    public void accept_delete_auth_fail_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(AnnualDuty.builder()
                .user(taeheoki)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(5))
                .status(StatusConst.ACCEPTED)
                .type(true)
                .title(taeheoki.getUsername() + "의 일정")
                .updateStatus(StatusConst.UPDATE_DELETESTATUS)
                .build());

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/accept/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 승인 - 유효한 토큰은 있으나 삭제된 유저일 경우")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_delete_non_exist_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(AnnualDuty.builder()
                .user(taeheoki)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(5))
                .status(StatusConst.ACCEPTED)
                .type(true)
                .title(taeheoki.getUsername() + "의 일정")
                .updateStatus(StatusConst.UPDATE_DELETESTATUS)
                .build());
        User admin = userRepository.findByEmail("admin@nate.com").get();
        userRepository.delete(admin);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/accept/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("등록되지 않은 유저입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 거절 - 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_reject_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(AnnualDuty.builder()
                .user(taeheoki)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(5))
                .status(StatusConst.ACCEPTED)
                .type(true)
                .title(taeheoki.getUsername() + "의 일정")
                .updateStatus(StatusConst.UPDATE_DELETESTATUS)
                .build());

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/reject/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print()).andDo(document);
        Assertions.assertThat(annualDutyPS.getStatus()).isEqualTo(StatusConst.ACCEPTED);
    }

    @DisplayName("일정 삭제 거절 - 인증 실패")
    @Test
    public void delete_reject_auth_fail_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(AnnualDuty.builder()
                .user(taeheoki)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(5))
                .status(StatusConst.ACCEPTED)
                .type(true)
                .title(taeheoki.getUsername() + "의 일정")
                .updateStatus(StatusConst.UPDATE_DELETESTATUS)
                .build());

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/reject/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 거절 - 유효한 토큰은 있으나 삭제된 유저일 경우")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_reject_non_exist_test() throws Exception {
        // given
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newAD(taeheoki));
        User admin = userRepository.findByEmail("admin@nate.com").get();
        userRepository.delete(admin);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/reject/" + annualDutyPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("등록되지 않은 유저입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 업데이트 승인 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_accept_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/accept/" + updateRequestLogPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);
        log.info("start={}", start);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.id").value(annualDutyPS.getId()))
                .andExpect(jsonPath("$.data.title").value(taeheoki.getUsername() + "의 일정 수정"))
                .andExpect(jsonPath("$.data.startTime").value(start.toString()))
                .andExpect(jsonPath("$.data.endTime").value(end.toString()))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
        Assertions.assertThat(updateRequestLogPS.getStatus()).isTrue();
    }

    @DisplayName("일정 업데이트 승인 - 인증 실패")
    @Test
    public void accept_update_auth_fail_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/accept/" + updateRequestLogPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 업데이트 승인 - 유효한 토큰은 있으나 삭제된 유저일 경우")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_update_non_exist_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));

        User admin = userRepository.findByEmail("admin@nate.com").get();
        userRepository.delete(admin);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/accept/" + updateRequestLogPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("등록되지 않은 유저입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 업데이트 승인 - 존재하지 않는 수정 요청 실패")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_update_non_exist_update_request_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/accept/" + 1L));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("수정사항이 존재하지 않습니다. "))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 수정 거절 - 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void reject_update_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));


        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/reject/" + updateRequestLogPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print()).andDo(document);
        Assertions.assertThat(annualDutyPS.getStatus()).isEqualTo(StatusConst.ACCEPTED);
        Assertions.assertThat(updateRequestLogPS.getStatus()).isTrue();
    }

    @DisplayName("일정 수정 거절 - 인증 실패")
    @Test
    public void reject_update_auth_fail_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));


        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/reject/" + updateRequestLogPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 거절 - 유효한 토큰은 있으나 삭제된 유저일 경우")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_reject_non_exist_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));

        User admin = userRepository.findByEmail("admin@nate.com").get();
        userRepository.delete(admin);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/delete/reject/" + updateRequestLogPS.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("등록되지 않은 유저입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 업데이트 승인 - 존재하지 않는 수정 요청 실패")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void reject_update_non_exist_update_request_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/admin/update/reject/" + 1L));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("수정사항이 존재하지 않습니다. "))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("등록 요청 데이터조회 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void save_request_list_test() throws Exception {
        // given
        User user = userRepository.findByEmail("ssar@nate.com").get();
        for (int i = 0; i < 10; i++)
            annualDutyRepository.save(dummy.newAD(user));

        // when
        ResultActions resultActions = mvc
                .perform(get("/s/admin/save?page=0"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.content.length()").value(8))
                .andExpect(jsonPath("$.data.content[0].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[1].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[2].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[3].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[4].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[5].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[6].status").value(StatusConst.APPROVING))
                .andExpect(jsonPath("$.data.content[7].status").value(StatusConst.APPROVING))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("수정 요청 데이터 조회 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_request_list_test() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.parse("2023-05-13T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse("2023-05-20T10:30:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        AnnualDuty annualDutyPS = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle(taeheoki.getUsername() + "의 일정 수정");
        updateInDTO.setStart(start);
        updateInDTO.setEnd(end);
        for (int i = 0; i < 10; i++) {
            updateRequestLogRepository.save(updateInDTO.toEntity(annualDutyPS));
        }

        // when
        ResultActions resultActions = mvc
                .perform(get("/s/admin/update"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.content.length()").value(8))
                .andExpect(jsonPath("$.data.content[0].status").value(false))
                .andExpect(jsonPath("$.data.content[1].status").value(false))
                .andExpect(jsonPath("$.data.content[2].status").value(false))
                .andExpect(jsonPath("$.data.content[3].status").value(false))
                .andExpect(jsonPath("$.data.content[4].status").value(false))
                .andExpect(jsonPath("$.data.content[5].status").value(false))
                .andExpect(jsonPath("$.data.content[6].status").value(false))
                .andExpect(jsonPath("$.data.content[7].status").value(false))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("삭제 요청 데이터 조회 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_request_list_test() throws Exception {
        // given
        User user = userRepository.findByEmail("ssar@nate.com").get();
        for (int i = 0; i < 10; i++) {
            annualDutyRepository.save(AnnualDuty.builder()
                    .user(user)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(5))
                    .status("1")
                    .type(true)
                    .title(user.getUsername() + "의 일정")
                    .updateStatus(StatusConst.UPDATE_DELETESTATUS)
                    .build());
        }

        // when
        ResultActions resultActions = mvc
                .perform(get("/s/admin/delete?page=0"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.content.length()").value(8))
                .andExpect(jsonPath("$.data.content[0].updateStatus").value(StatusConst.UPDATE_DELETESTATUS))
                .andExpect(jsonPath("$.data.content[1].updateStatus").value(StatusConst.UPDATE_DELETESTATUS))
                .andExpect(jsonPath("$.data.content[2].updateStatus").value(StatusConst.UPDATE_DELETESTATUS))
                .andExpect(jsonPath("$.data.content[3].updateStatus").value(StatusConst.UPDATE_DELETESTATUS))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("전체 유저 조회 - 성공")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void users_list_test() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            userRepository.save(dummy.newUser("user" + i, "USER"));
        }

        // when
        ResultActions resultActions = mvc
                .perform(get("/s/admin/users"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.content.length()").value(8))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
