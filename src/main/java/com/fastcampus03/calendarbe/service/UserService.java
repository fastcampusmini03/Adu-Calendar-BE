package com.fastcampus03.calendarbe.service;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception400;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.core.exception.Exception500;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.user.UserRequest;
import com.fastcampus03.calendarbe.model.log.login.LoginLog;
import com.fastcampus03.calendarbe.model.log.login.LoginLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> 로그인(UserRequest.LoginInDTO loginInDTO, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            User userPS = myUserDetails.getUser();
            String jwt = MyJwtProvider.create(userPS);

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
        } catch (Exception e) {
            throw new Exception401("인증되지 않았습니다");
        }
    }

    @Transactional
    public UserRequest.LoginInDTO 회원가입(UserRequest.JoinInDTO joinInDTO) {
        Optional<User> userOP = userRepository.findByEmail(joinInDTO.getEmail());
        if(userOP.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception400에게 제어권을 뺏긴다.
            throw new Exception400("email", "이메일이 존재합니다.");
        }
        String rawPassword = joinInDTO.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword); // 60Byte
        joinInDTO.setPassword(encPassword);
        try {
            User userPS = userRepository.save(joinInDTO.toEntity());
            return joinInDTO.toLoginInDTO(rawPassword);
        } catch (RuntimeException e) {
            throw new Exception500("회원 가입에 실패하였습니다." + e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO<?> 회원정보수정(Long id, UserRequest.UpdateInDTO updateInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception401("존재하지 않는 회원입니다.")
        );

        // 현재 인증된 사용자의 정보를 가져옵니다.
        User user = myUserDetails.getUser();

        // 현재 인증된 사용자가 수정하려는 사용자의 ID와 같은지 확인합니다.
        if (!user.getId().equals(userPS.getId())) {
            throw new Exception401("인증되지 않았습니다.");
        }

        // 수정하려는 사용자의 정보를 업데이트합니다.
        userPS.updateInfo(passwordEncoder.encode(updateInDTO.getPassword()), updateInDTO.getUsername());
        return new ResponseDTO<>(userPS);
    }

    public ResponseDTO<?> 회원정보보기(Long id, MyUserDetails myUserDetails) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception401("존재하지 않는 회원입니다.")
        );

        // 현재 인증된 사용자의 정보를 가져옵니다.
        User user = myUserDetails.getUser();

        // 현재 인증된 사용자가 수정하려는 사용자의 ID와 같은지 확인합니다.
        if (!user.getId().equals(userPS.getId())) {
            throw new Exception401("인증되지 않았습니다.");
        }
        return new ResponseDTO<>(userPS);
    }
}
