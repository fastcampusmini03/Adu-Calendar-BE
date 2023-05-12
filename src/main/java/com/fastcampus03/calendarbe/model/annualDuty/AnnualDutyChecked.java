package com.fastcampus03.calendarbe.model.annualDuty;

import com.fastcampus03.calendarbe.core.util.TimeBaseEntity;
import com.fastcampus03.calendarbe.dto.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class AnnualDutyChecked extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_shown", nullable = false)
    private Boolean isShown; // 0: 안보여줬다, 1: 보여줬다.

    @Column(nullable = false)
    private String msg;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private AnnualDuty annualDuty;
    public void afterUserCheck() {
        this.isShown = true;
    }

    public UserResponse.AnnualDutyCheckOutDTO toAnnualDutyCheckOutDTO() {
        return UserResponse.AnnualDutyCheckOutDTO
                .builder()
                .id(id)
                .msg(msg)
                .annualDuty(annualDuty.toAnnualDutyAnnualDutyCheckOutDTO())
                .build();
    }
}
