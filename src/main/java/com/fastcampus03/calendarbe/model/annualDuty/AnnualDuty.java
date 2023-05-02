package com.fastcampus03.calendarbe.model.annualDuty;

import com.fastcampus03.calendarbe.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String content;
    private String type;

    private LocalDateTime start;
    private LocalDateTime end;


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
