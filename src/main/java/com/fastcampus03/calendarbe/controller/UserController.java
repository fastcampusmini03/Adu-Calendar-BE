package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
import com.fastcampus03.calendarbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;


@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid  UserRequest.LoginInDTO loginInDTO, HttpServletRequest request) {
        Map<String, Object> responseData = userService.로그인(loginInDTO, request);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(responseData.get("loginUser"));
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, (String) responseData.get("jwt")).body(responseDTO);
    }
}
