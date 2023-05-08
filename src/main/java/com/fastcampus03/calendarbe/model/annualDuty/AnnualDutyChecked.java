package com.fastcampus03.calendarbe.model.annualDuty;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "annual_duty_checked_tb")
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
public class AnnualDutyChecked {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_shown", nullable = false)
    private Boolean isShown; // 0: 안보여줬다, 1: 보여줬다.

    @Column(nullable = false)
    private String msg;

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
