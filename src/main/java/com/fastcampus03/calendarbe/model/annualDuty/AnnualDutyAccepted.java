package com.fastcampus03.calendarbe.model.annualDuty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "annual_duty_accepted")
@NoArgsConstructor
@Entity
public class AnnualDutyAccepted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_shown", nullable = false)
    private Boolean isShown; // 0: 안보여줬다, 1: 보여줬다.

    @OneToOne(fetch = FetchType.LAZY)
    private AnnualDuty annualDuty;

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