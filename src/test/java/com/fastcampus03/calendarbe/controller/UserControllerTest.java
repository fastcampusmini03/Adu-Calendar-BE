package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.MyRestDoc;
import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.dummy.DummyEntity;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyChecked;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyCheckedRepository;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("회원 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest extends MyRestDoc {

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
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AnnualDutyRepository annualDutyRepository;
    @Autowired
    private AnnualDutyCheckedRepository annualDutyCheckedRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("ssar", "USER"));
        userRepository.save(dummy.newUser("cos", "USER"));
        userRepository.save(dummy.newUser("admin", "ADMIN"));
        em.clear();
    }

    @DisplayName("회원가입 성공")
    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setEmail("love@nate.com");
        joinInDTO.setPassword("1234");
        joinInDTO.setUsername("love");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         * 	"status":201,
         * 	"msg":"성공","data":
         *          {
         * 					"id":3,
         * 					"email":"love@nate.com",
         * 					"username":"love",
         * 					"role":"USER",
         * 					"createdAt":"2023-05-09T19:50:54.17361",
         * 					"updatedAt":"2023-05-09T19:50:54.3871718",
         * 					"loggedInAt":"2023-05-09T19:50:54.3828251"
         *            }
         * }
         */
        // then
        resultActions.andExpect(jsonPath("$.data.id").value(4L));
        resultActions.andExpect(jsonPath("$.data.username").value("love"));
        resultActions.andExpect(jsonPath("$.data.role").value("USER"));
        resultActions.andExpect(status().isCreated());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 실패-email(존재하는 email)")
    @Test
    public void join_fail_bad_request_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("ssar");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("ssar@nate.com");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         *      "status":400,
         *      "msg":"badRequest",
         *      "data":
         *          {
         *              "key":"email",
         *              "value":"이메일이 존재합니다."
         *          }
         *  }
         */

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일이 존재합니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 유효성 검사 실패-email형식오류")
    @Test
    public void join_fail_valid_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("ssar");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("ssarnate.com");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         *  "status":400,
         *  "msg":"badRequest",
         *  "data":
         *      {
         *          "key":"email",
         *          "value":"이메일 형식으로 작성해주세요"
         *      }
         * }
         */

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일 형식으로 작성해주세요"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("로그인 성공")
    @Test
    public void login_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("ssar@nate.com");
        loginInDTO.setPassword("1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         *      "status":200,
         *      "msg":"성공",
         *      "data":
         *          {
         *              "id":1,
         *              "email":"ssar@nate.com",
         *              "password":"$2a$10$TrCtLIVnnrsuhl63CN66qedEamAjn9PabVkWmrvRGklAjdkz/xm.i",
         *              "username":"ssar",
         *              "role":"ADMIN",
         *              "createdAt":"2023-05-04T00:45:14.77679",
         *              "updatedAt":"2023-05-04T00:45:15.609451",
         *              "loggedInAt":"2023-05-04T00:45:15.60445"
         *          }
         * }
         */

        // then
        String jwtToken = resultActions.andReturn().getResponse().getHeader(MyJwtProvider.HEADER);
        Assertions.assertThat(jwtToken.startsWith(MyJwtProvider.TOKEN_PREFIX)).isTrue();
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("로그인 인증 실패")
    @Test
    public void login_fail_un_authorized_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setPassword("12345");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         *  "status":401,
         *  "msg":"unAuthorized",
         *  "data":"인증되지 않았습니다"
         * }
         */

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행전에 수행)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행전에 수행)
    // @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // authenticationManager.authenticate() 실행해서 MyUserDetailsService를 호출하고
    // usrename=ssar을 찾아서 세션에 담아주는 어노테이션
    @DisplayName("회원상세보기 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void detail_test() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/s/user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         * 		"status":200,
         * 		"msg":"성공",
         * 		"data":
         *              {
         * 						"id":1,
         * 						"email":"ssar@nate.com",
         * 						"username":"ssar",
         * 						"role":"USER",
         * 						"createdAt":"2023-05-09T19:58:36.893548",
         * 						"updatedAt":null,"loggedInAt":null
         *                }
         * }
         */

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
        resultActions.andExpect(jsonPath("$.data.email").value("ssar@nate.com"));
        resultActions.andExpect(jsonPath("$.data.role").value("USER"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원상세보기 인증 실패")
    @Test
    public void detail_fail_un_authorized_test() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/s/user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        /**
         * {
         * 		"status":401,
         * 		"msg":"unAuthorized",
         * 		"data":"인증되지 않았습니다"
         * }
         */

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("업데이트 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        User ssar = userRepository.findByEmail("ssar@nate.com").get();
        String token = MyJwtProvider.create(ssar);

        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setUsername("archie");
        updateInDTO.setPassword("5678");
        String requestBody = om.writeValueAsString(updateInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user")
                        .header("Authorization", "Bearer " + token)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.id").value(ssar.getId()))
                .andExpect(jsonPath("$.data.username").value("archie"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
        Assertions.assertThat(passwordEncoder.matches("5678", ssar.getPassword())).isEqualTo(true);
    }

    @DisplayName("회원 정보 수정 - 인증 실패")
    @Test
    public void update_non_verify_test() throws Exception {
        // given
        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setUsername("archie");
        updateInDTO.setPassword("5678");
        String requestBody = om.writeValueAsString(updateInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("인증되지 않았습니다"))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원 정보 수정 실패 - 토큰은 유효하나 존재하지 않는 유저")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_token_non_exist_user_test() throws Exception {
        // given
        User ssar = userRepository.findByEmail("ssar@nate.com").get();
        String token = MyJwtProvider.create(ssar);
        userRepository.delete(ssar);

        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setUsername("archie");
        updateInDTO.setPassword("5678");
        String updateRequestBody = om.writeValueAsString(updateInDTO);


        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user")
                        .header("Authorization", "Bearer " + token)
                        .content(updateRequestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("responseBody={}", responseBody);

        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("존재하지 않는 회원입니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("요청 결과 확인")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void annual_duty_check_test() throws Exception {
        // given
        Map<String, List<Long>> requestData = new HashMap<>();
        List<Long> adcIds = new ArrayList<>();

        User userPS = userRepository.findByEmail("ssar@nate.com").get();
        String token = MyJwtProvider.create(userPS);

        for (int i = 0; i < 3; i++) {
            AnnualDuty annualDuty = dummy.newADWithApproved(userPS);
            AnnualDuty annualDutyPS = annualDutyRepository.save(annualDuty);
            AnnualDutyChecked annualDutyChecked = annualDutyCheckedRepository.save(dummy.newADC(annualDutyPS));
            adcIds.add(annualDutyChecked.getId());
        }
        requestData.put("updateRequestLogList", adcIds);
        String requestBody = om.writeValueAsString(requestData);

        // when
        ResultActions resultActions = mvc
                .perform(get("/s/user/annualDutyCheck")
                        .header("Authorization", "Bearer " + token)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.length()").value(3))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("요청 결과 확인 후 처리")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void annualDutyCheckUpdate_test() throws Exception {
        // given
        Map<String, List<Long>> requestData = new HashMap<>();
        List<Long> adcIds = new ArrayList<>();

        User userPS = userRepository.findByEmail("ssar@nate.com").get();
        String token = MyJwtProvider.create(userPS);

        for (int i = 0; i < 3; i++) {
            AnnualDuty annualDuty = dummy.newADWithApproved(userPS);
            AnnualDuty annualDutyPS = annualDutyRepository.save(annualDuty);
            AnnualDutyChecked annualDutyChecked = annualDutyCheckedRepository.save(dummy.newADC(annualDutyPS));
            adcIds.add(annualDutyChecked.getId());
        }
        requestData.put("updateRequestLogList", adcIds);
        String requestBody = om.writeValueAsString(requestData);

        // when
        ResultActions resultActions = mvc
                .perform(post("/s/user/annualDutyCheck")
                        .header("Authorization", "Bearer " + token)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print()).andDo(document);

        for (Long adcId : adcIds) {
            AnnualDutyChecked annualDutyChecked = annualDutyCheckedRepository.findById(adcId).get();
            Assertions.assertThat(annualDutyChecked.getIsShown()).isTrue();
        }
    }
}
