package com.fastcampus03.calendarbe.model.annualDuty;

import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.util.StatusConst;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "annual_duty_tb")
@Entity
public class AnnualDuty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5)
    private String status; // 승인중 / 승인 / 거절

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, length = 100)
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

    private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

    public void updateAnnualDuty(String title, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void approvingStatus() {
        this.status = StatusConst.APPROVING;
    }

    public void approvedStatus() {
        this.status = StatusConst.ACCEPTED;
    }

    public void rejectedStatus() {
        this.status = StatusConst.REJECTED;
    }

    public void afterRequestProcess() {
        this.updateStatus = StatusConst.UPDATE_DEFAULTSTATUS;
    }

    public void updateRequest() {
        this.updateStatus = StatusConst.UPDATE_UPDATESTATUS;
    }

    public void deleteRequest() {
        this.updateStatus = StatusConst.UPDATE_DELETESTATUS;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
