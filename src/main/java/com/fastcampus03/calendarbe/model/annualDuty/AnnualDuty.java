package com.fastcampus03.calendarbe.model.annualDuty;

import com.fastcampus03.calendarbe.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "annual_duty_tb")
@NoArgsConstructor
@Entity
public class AnnualDuty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5)
    private String calendarId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean type; // false: 당직, true: 연차

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
