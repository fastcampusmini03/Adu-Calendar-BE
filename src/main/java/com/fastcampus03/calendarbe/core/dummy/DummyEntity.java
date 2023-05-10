package com.fastcampus03.calendarbe.core.dummy;

import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username, String role){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(role)
                .build();
    }

    public AnnualDuty newAD(User user){
        return AnnualDuty.builder()
                .user(user)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(5))
                .status("0")
                .type(true)
                .title(user.getUsername() + "의 일정")
                .build();
    }

    public User newMockUser(Long id, String username, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
