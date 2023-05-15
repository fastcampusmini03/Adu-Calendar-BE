package com.fastcampus03.calendarbe.model.log.login;

import com.fastcampus03.calendarbe.core.util.TimeBaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "login_log_tb")
@Entity
public class LoginLog extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String userAgent;
    private String clientIP;

    @Builder
    public LoginLog(Long id, Long userId, String userAgent, String clientIP, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userAgent = userAgent;
        this.clientIP = clientIP;
    }
}
