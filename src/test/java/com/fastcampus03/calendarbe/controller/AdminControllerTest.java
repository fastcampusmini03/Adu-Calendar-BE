package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.MyRestDoc;
import com.fastcampus03.calendarbe.core.dummy.DummyEntity;
import com.fastcampus03.calendarbe.dto.admin.AdminRequest;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@DisplayName("관리자 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(MockitoJUnitRunner.class)
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
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("ssar", "USER"));
        userRepository.save(dummy.newUser("cos", "USER"));
        userRepository.save(dummy.newUser("admin", "ADMIN"));
        em.clear();
    }

    /**
     * question: ADMIN 권한으로 들어 갔을 때 updateRole()의 역할
     * ADMIN -> USER ?
     * USER -> ADMIN ?
     */

    @Test
    public void updateRoleTest() throws Exception {

        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("admin@nate.com");
        loginInDTO.setPassword("1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        AdminRequest.UpdateRoleDTO updateRoleDTO = new AdminRequest.UpdateRoleDTO();

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

    }

//    @PostMapping("/s/admin/save/accept/{id}")
//    public ResponseEntity<?> acceptSave(
//            @PathVariable("id") Long saveId,
//            @AuthenticationPrincipal MyUserDetails myUserDetails){
//        ResponseDTO<?> responseDTO = adminService.일정등록요청승인(saveId, myUserDetails);
//        return ResponseEntity.ok().body(responseDTO);
//    }


    @DisplayName("알정등록요청승인")
    @WithMockUser(value = "admin@nate.com", roles = "ADMIN")
    @Test
    public void acceptSaveTest() throws Exception {

        // given
        Long saveId = 1L;

        ResultActions resultActions = mvc
                .perform(post("/s/admin/save/accept/" + saveId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);
    }
}
