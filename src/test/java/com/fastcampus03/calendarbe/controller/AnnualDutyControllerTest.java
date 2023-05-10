package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.MyRestDoc;
import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.dummy.DummyEntity;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@DisplayName("일정관리 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AnnualDutyControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @Autowired
    private AnnualDutyRepository annualDutyRepository;

    @BeforeEach
    void before() {
        User taeheoki = userRepository.save(dummy.newUser("taeheoki", "USER"));
        User ssar = userRepository.save(dummy.newUser("ssar", "USER"));
        User admin = userRepository.save(dummy.newUser("admin", "ADMIN"));
        for (int i = 0; i < 2; i++) {
            annualDutyRepository.save(dummy.newAD(taeheoki));
            annualDutyRepository.save(dummy.newADWithApproved(taeheoki));
            annualDutyRepository.save(dummy.newAD(ssar));
            annualDutyRepository.save(dummy.newADWithApproved(taeheoki));
        }
    }

    @DisplayName("일정 등록 요청")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void save_test() throws Exception {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        AnnualDutyRequest.SaveInDTO saveInDTO = new AnnualDutyRequest.SaveInDTO();
        saveInDTO.setCalendarId("0");
        saveInDTO.setUsername("taeheoki");
        saveInDTO.setEmail("taeheoki@nate.com");
        saveInDTO.setTitle("taeheoki의 일정");

        LocalDateTime startTime = LocalDateTime.now();
        saveInDTO.setStart(startTime);

        LocalDateTime endTime = LocalDateTime.now().plusDays(5);
        saveInDTO.setEnd(endTime);

        saveInDTO.setIsAllday(true);
        saveInDTO.setRole("USER");
        String requestBody = om.writeValueAsString(saveInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/save").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.user.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("taeheoki의 일정"))
                .andExpect(jsonPath("$.data.startTime").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.endTime").value(endTime.format(formatter)))
                .andExpect(jsonPath("$.data.type").value(true))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 등록 요청 실패 - 종료가 시작보다 빠름")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void save_bad_request_test() throws Exception {
        // given
        AnnualDutyRequest.SaveInDTO saveInDTO = new AnnualDutyRequest.SaveInDTO();
        saveInDTO.setCalendarId("0");
        saveInDTO.setUsername("taeheoki");
        saveInDTO.setEmail("taeheoki@nate.com");
        saveInDTO.setTitle("taeheoki의 일정");
        saveInDTO.setStart(LocalDateTime.now());
        saveInDTO.setEnd(LocalDateTime.now().minusDays(5));
        saveInDTO.setIsAllday(true);
        saveInDTO.setRole("USER");
        String requestBody = om.writeValueAsString(saveInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/save").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("endTimeAfterStartTime"))
                .andExpect(jsonPath("$.data.value").value( "End time should be after start time"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 수정 요청")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateAnnualDuty_test() throws Exception {
        // given
        User taeheoki = userRepository.findByEmail("taeheoki@nate.com").get();
        AnnualDuty annualDuty = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle("taeheoki의 일정 수정");
        LocalDateTime startTime = LocalDateTime.now();
        updateInDTO.setStart(startTime);
        LocalDateTime endTime = LocalDateTime.now().plusDays(15);
        updateInDTO.setEnd(endTime);
        String requestBody = om.writeValueAsString(updateInDTO);
        log.info("requestBody={}", requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/update/" + annualDuty.getId()).content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("taeheoki의 일정 수정"))
                .andExpect(jsonPath("$.data.startTime").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.endTime").value(endTime.format(formatter)))
                .andExpect(jsonPath("$.data.status").value(false))
                .andExpect(jsonPath("$.data.annualDuty.id").value(annualDuty.getId()))
                .andExpect(jsonPath("$.data.annualDuty.updateStatus").value(1))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 수정 요청 실패 - 종료가 시작보다 빠름")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateAnnualDuty_bad_request_test() throws Exception {
        // given
        User taeheoki = userRepository.findByEmail("taeheoki@nate.com").get();
        AnnualDuty annualDuty = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle("taeheoki의 일정 수정");
        LocalDateTime startTime = LocalDateTime.now();
        updateInDTO.setStart(startTime);
        LocalDateTime endTime = LocalDateTime.now().minusDays(15);
        updateInDTO.setEnd(endTime);
        String requestBody = om.writeValueAsString(updateInDTO);
        log.info("requestBody={}", requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/update/" + annualDuty.getId()).content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("endTimeAfterStartTime"))
                .andExpect(jsonPath("$.data.value").value( "End time should be after start time"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 수정 요청 실패 - 존재하지 않는 일정 수정 요청")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateAnnualDuty_non_exist_test() throws Exception {
        // given
        Long id = Long.MAX_VALUE;

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle("taeheoki의 일정 수정");
        LocalDateTime startTime = LocalDateTime.now();
        updateInDTO.setStart(startTime);
        LocalDateTime endTime = LocalDateTime.now().plusDays(15);
        updateInDTO.setEnd(endTime);
        String requestBody = om.writeValueAsString(updateInDTO);
        log.info("requestBody={}", requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/update/" + id).content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value( "존재하지 않는 일정입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 수정 요청 실패 - 권한이 부여되지 않은 유저 수정 요청")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateAnnualDuty_non_verify_test() throws Exception {
        // given
        User taeheoki = userRepository.findByEmail("taeheoki@nate.com").get();
        AnnualDuty annualDuty = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        AnnualDutyRequest.UpdateInDTO updateInDTO = new AnnualDutyRequest.UpdateInDTO();
        updateInDTO.setTitle("taeheoki의 일정 수정");
        LocalDateTime startTime = LocalDateTime.now();
        updateInDTO.setStart(startTime);
        LocalDateTime endTime = LocalDateTime.now().plusDays(15);
        updateInDTO.setEnd(endTime);
        String requestBody = om.writeValueAsString(updateInDTO);
        log.info("requestBody={}", requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/update/" + annualDuty.getId()).content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value( "본인의 일정만 수정할 수 있습니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 요청")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteAnnualDuty_test() throws Exception {
        // given
        User taeheoki = userRepository.findByEmail("taeheoki@nate.com").get();
        AnnualDuty annualDuty = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/delete/" + annualDuty.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 요청 실패 - 존재하지 않는 일정 수정 요청")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteAnnualDuty_non_exist_test() throws Exception {
        // given
        Long id = Long.MAX_VALUE;

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/delete/" + id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("id"))
                .andExpect(jsonPath("$.data.value").value("존재하지 않는 일정입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 삭제 요청 실패 - 권한이 부여되지 않은 유저 수정 요청")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteAnnualDuty_non_verify_test() throws Exception {
        // given
        User taeheoki = userRepository.findByEmail("taeheoki@nate.com").get();
        AnnualDuty annualDuty = annualDutyRepository.save(dummy.newADWithApproved(taeheoki));

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDuty/delete/" + annualDuty.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("본인의 일정만 수정할 수 있습니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 조회 요청 - 토큰X")
    @Test
    void showAnnualDutyList_non_token_test() throws Exception {
        // given
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();

        // when
        ResultActions resultActions = mvc
                .perform(get("/annualDuty/?year=" + year + "&month=" + month));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.length()").value(4))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 조회 요청 - 유저 토큰")
    @WithUserDetails(value = "taeheoki@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void showAnnualDutyList_user_token_test() throws Exception {
        // given
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();

        User user = User.builder().id(2L).role("USER").build();
        String jwt = MyJwtProvider.create(user);

        // when
        ResultActions resultActions = mvc
                .perform(get("/annualDuty/?year=" + year + "&month=" + month)
                        .header("Authorization", "Bearer " + jwt));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.length()").value(6))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("일정 조회 요청 - 관리자 토큰")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void showAnnualDutyList_admin_token_test() throws Exception {
        // given
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();

        User user = User.builder().id(3L).role("ADMIN").build();
        String jwt = MyJwtProvider.create(user);

        // when
        ResultActions resultActions = mvc
                .perform(get("/annualDuty/?year=" + year + "&month=" + month)
                        .header("Authorization", "Bearer " + jwt));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.length()").value(8))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}