//package com.fastcampus03.calendarbe.controller;
//
//import com.fastcampus03.calendarbe.core.MyRestDoc;
//import com.fastcampus03.calendarbe.core.dummy.DummyEntity;
//import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
//import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
//import com.fastcampus03.calendarbe.model.user.User;
//import com.fastcampus03.calendarbe.model.user.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.TestExecutionEvent;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//@Slf4j
//@DisplayName("관리자 API")
//@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
//@ActiveProfiles("test")
//@Sql("classpath:db/teardown.sql")
//@AutoConfigureMockMvc
//@Transactional
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class AdminControllerTest extends MyRestDoc {
//
//    private DummyEntity dummy = new DummyEntity();
//
//    @Autowired
//    private MockMvc mvc;
//    @Autowired
//    private ObjectMapper om;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private AnnualDutyRepository annualDutyRepository;
//    @Autowired
//    private EntityManager em;
//
//    @BeforeEach
//    public void setUp() {
//        User ssar = userRepository.save(dummy.newUser("ssar", "USER"));
//        User admin = userRepository.save(dummy.newUser("admin", "ADMIN"));
//        for (int i = 0; i < 15; i++)
//            annualDutyRepository.save(dummy.newAD(ssar));
//        em.clear();
//    }
//
//    @DisplayName("일정 등록 승인")
//    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void acceptSave_test() throws Exception {
//        // given
//        Long id = 1L;
//
//        // when
//        // 반환데이터 확인
//        ResultActions resultActions = mvc
//                .perform(post("/s/admin/save/accept/" + id));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        log.info("responseBody={}", responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data.id").value(1L));
//        // 승인중 -> 승인
//        resultActions.andExpect(jsonPath("$.data.status").value("1"));
//    }
//
//    @DisplayName("일정 등록 거절")
//    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void acceptReject_test() throws Exception {
//        // given
//        Long id = 2L;
//
//        // when
//        // 반환데이터 확인
//        ResultActions resultActions = mvc
//                .perform(post("/s/admin/save/reject/" + id));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        log.info("responseBody={}", responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data").isEmpty());
//    }
//
//    @DisplayName("일정등록")
//    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void save() throws Exception {
//
//        AnnualDutyRequest.SaveInDTO saveInDTO = new AnnualDutyRequest.SaveInDTO();
//        saveInDTO.setCalendarId("0");
//        saveInDTO.setUsername("ssar");
//        saveInDTO.setEmail("ssar@nate.com");
//        saveInDTO.setTitle("ssar의 휴가");
//        saveInDTO.setIsAllday(true);
//
//        String startDateTimeString = "2023-05-12T11:00:00";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
//        saveInDTO.setStart(startDateTime);
//
//        String endDateTimeString = "2023-05-12T12:00:00";
//        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, formatter);
//        saveInDTO.setEnd(endDateTime);
//
//        saveInDTO.setIsAllday(true);
//        saveInDTO.setRole("USER");
//
//        String requestBody = om.writeValueAsString(saveInDTO);
//
//        // when
//        // 반환데이터 확인
//        ResultActions resultActions = mvc
//                .perform(post("/s/user/annualDuty/save")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("디버그 : " + responseBody);
//
//        // {
//        //      "status":200,
//        //      "msg":"성공",
//        //      "data":{
//        //                  "id":1,
//        //                  "status":"0",
//        //                  "user":
//        //                      {
//        //                          "id":1,
//        //                          "email":"ssar@nate.com",
//        //                          "username":"ssar",
//        //                          "role":"USER",
//        //                          "createdAt":"2023-05-11T08:59:32.365579",
//        //                          "updatedAt":null,
//        //                          "loggedInAt":null
//        //                      },
//        //                  "title":"ssar의 휴가",
//        //                  "startTime":"2023-05-12T11:00:00",
//        //                  "endTime":"2023-05-12T12:00:00",
//        //                  "type":true,
//        //                  "createdAt":"2023-05-11T08:59:32.9197651",
//        //                  "updatedAt":null,"updateStatus":null
//        //              }
//        // }
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data.id").value(1L));
//        resultActions.andExpect(jsonPath("$.data.type").value("true"));
//    }
//}
