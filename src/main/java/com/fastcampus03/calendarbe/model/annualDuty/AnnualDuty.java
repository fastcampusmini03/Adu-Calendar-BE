package com.fastcampus03.calendarbe.model.annualDuty;

import com.fastcampus03.calendarbe.core.util.TimeBaseEntity;
import com.fastcampus03.calendarbe.dto.admin.AdminResponse;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyResponse;
import com.fastcampus03.calendarbe.dto.user.UserResponse;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.core.util.StatusConst;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "annual_duty_tb")
@Entity
public class AnnualDuty extends TimeBaseEntity {
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

    public AdminResponse.AcceptSaveOutDTO toAcceptSaveOutDTO() {
        return AdminResponse.AcceptSaveOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAcceptSaveOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AdminResponse.AcceptUpdateOutDTO toAcceptUpdateOutDTO() {
        return AdminResponse.AcceptUpdateOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAcceptUpdateOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AdminResponse.AnnualDutySaveRequestListOutDTO toAnnualDutySaveRequestListOutDTO() {
        return AdminResponse.AnnualDutySaveRequestListOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAnnualDutySaveRequestListOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AdminResponse.AnnualDutyDeleteRequestListOutDTO toAnnualDutyDeleteRequestListOutDTO() {
        return AdminResponse.AnnualDutyDeleteRequestListOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAnnualDutyDeleteRequestListOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AdminResponse.UpdateRequestLogUpdateRequestListOutDTO.AnnualDutyUpdateRequestLogUpdateRequestListOutDTO toAnnualDutyUpdateRequestLogUpdateRequestListOutDTO() {
        return AdminResponse.UpdateRequestLogUpdateRequestListOutDTO.AnnualDutyUpdateRequestLogUpdateRequestListOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAnnualDutyUpdateRequestLogUpdateRequestListOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public UserResponse.AnnualDutyCheckOutDTO.AnnualDutyAnnualDutyCheckOutDTO toAnnualDutyAnnualDutyCheckOutDTO() {
        return UserResponse.AnnualDutyCheckOutDTO.AnnualDutyAnnualDutyCheckOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAnnualDutyAnnualDutyCheckOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AnnualDutyResponse.SaveOutDTO toSaveOutDTO() {
        return AnnualDutyResponse.SaveOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserSaveOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AnnualDutyResponse.UpdateAnnualDutyOutDTO.AnnualDutyUpdateAnnualDutyOutDTO toAnnualDutyUpdateAnnualDutyOutDTO() {
        return AnnualDutyResponse.UpdateAnnualDutyOutDTO.AnnualDutyUpdateAnnualDutyOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserAnnualDutyUpdateAnnualDutyOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }

    public AnnualDutyResponse.ShowAnnualDutyListOutDTO toShowAnnualDutyListOutDTO() {
        return AnnualDutyResponse.ShowAnnualDutyListOutDTO
                .builder()
                .id(id)
                .status(status)
                .user(user.toUserShowAnnualDutyListOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .updateStatus(updateStatus)
                .build();
    }
}
