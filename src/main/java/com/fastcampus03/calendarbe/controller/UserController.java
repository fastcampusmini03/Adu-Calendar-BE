package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
import com.fastcampus03.calendarbe.dto.user.UserResponse;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyChecked;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO, HttpServletRequest request) {
        Map<String, Object> responseData = userService.로그인(loginInDTO, request);
        User user = (User) responseData.get("loginUser");
        ResponseDTO<?> responseDTO = new ResponseDTO<>(user.toLoinOutDTO());
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, (String) responseData.get("jwt")).body(responseDTO);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join (
            @RequestBody @Valid UserRequest.JoinInDTO joinInDTO,
            Errors errors,
            HttpServletRequest request) {
        Map<String, Object> responseData = userService.회원가입(joinInDTO, request);
        User user = (User) responseData.get("loginUser");
        ResponseDTO<?> responseDTO = new ResponseDTO<>(HttpStatus.CREATED, "성공", user.toJoinOutDTO());
        return ResponseEntity.created(null).header(MyJwtProvider.HEADER, (String) responseData.get("jwt")).body(responseDTO);
    }

    @GetMapping("/s/user")
    public ResponseEntity<?> userInfo(
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        User user = userService.회원정보보기(myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(user.toUserInfoDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user")
    public ResponseEntity<?> update(
            @RequestBody @Valid UserRequest.UpdateInDTO updateInDTO,
            Errors errors,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        User user = userService.회원정보수정(updateInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(user.toUpdateDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/user/annualDutyCheck")
    public ResponseEntity<?> annualDutyCheck(
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        List<AnnualDutyChecked> annualDutyCheckedList = userService.요청결과확인조회(myUserDetails);
        List<UserResponse.AnnualDutyCheckOutDTO> annualDutyCheckOutDTOList
                = annualDutyCheckedList.stream().map(o -> o.toAnnualDutyCheckOutDTO()).collect(Collectors.toList());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDutyCheckOutDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDutyCheck")
    public ResponseEntity<?> annualDutyCheckUpdate(
            @RequestBody Map<String, List<Long>> requestData,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        List<Long> annualDutyCheckedList = requestData.get("annualDutyCheckedList");
        userService.요청결과확인(annualDutyCheckedList, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }
}
