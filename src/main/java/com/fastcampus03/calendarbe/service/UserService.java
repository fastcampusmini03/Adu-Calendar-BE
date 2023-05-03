package com.fastcampus03.calendarbe.service;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
import com.fastcampus03.calendarbe.model.log.login.LoginLog;
import com.fastcampus03.calendarbe.model.log.login.LoginLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final LoginLogRepository loginLogRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Map<String, Object> 로그인(UserRequest.LoginInDTO loginInDTO, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            User userPS = myUserDetails.getUser();
            String jwt =  MyJwtProvider.create(userPS);

            userPS.setLoggedInAt(LocalDateTime.now());
            LoginLog loginLog = LoginLog.builder()
                    .userId(userPS.getId())
                    .userAgent(request.getHeader("User-Agent"))
                    .clientIP(request.getRemoteAddr())
                    .build();
            loginLogRepository.save(loginLog);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("jwt", jwt);
            responseData.put("loginUser", userPS);

            return responseData;
        }catch (Exception e){
            throw new Exception401("인증되지 않았습니다");
        }
    }
}
