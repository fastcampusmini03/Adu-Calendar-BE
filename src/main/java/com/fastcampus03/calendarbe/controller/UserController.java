package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
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
import java.util.Map;


@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO, HttpServletRequest request) {
        Map<String, Object> responseData = userService.로그인(loginInDTO, request);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(responseData.get("loginUser"));
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, (String) responseData.get("jwt")).body(responseDTO);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join (
            @RequestBody @Valid UserRequest.JoinInDTO joinInDTO,
            Errors errors,
            HttpServletRequest request) {
        Map<String, Object> responseData = userService.회원가입(joinInDTO, request);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(HttpStatus.CREATED, "성공", responseData.get("loginUser"));
        return ResponseEntity.created(null).header(MyJwtProvider.HEADER, (String) responseData.get("jwt")).body(responseDTO);
    }

    @GetMapping("/s/user/{id}")
    public ResponseEntity<?> userInfo(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        ResponseDTO<?> responseDTO = userService.회원정보보기(id, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserRequest.UpdateInDTO updateInDTO,
            Errors errors,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        ResponseDTO<?> responseDTO = userService.회원정보수정(id, updateInDTO, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }
}
