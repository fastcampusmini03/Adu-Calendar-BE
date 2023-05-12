package com.fastcampus03.calendarbe.model.log.error;

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
@Table(name = "error_log_tb")
@Entity
public class ErrorLog extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 10000000)
    private String msg;
    @Column(nullable = false)
    private Long userId;

    @Builder
    public ErrorLog(Long id, String msg, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.msg = msg;
        this.userId = userId;
    }
}
