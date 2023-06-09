package com.fastcampus03.calendarbe.model.log.update;

import com.fastcampus03.calendarbe.core.util.TimeBaseEntity;
import com.fastcampus03.calendarbe.dto.admin.AdminResponse;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyResponse;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "update_request_log_tb")
@Entity
@Builder
public class UpdateRequestLog extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private AnnualDuty annualDuty;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean status; // true : 처리 후, false : 처리 전

    public AdminResponse.UpdateRequestLogUpdateRequestListOutDTO toUpdateRequestLogUpdateRequestListOutDTO() {
        return AdminResponse.UpdateRequestLogUpdateRequestListOutDTO
                .builder()
                .id(id)
                .annualDuty(annualDuty.toAnnualDutyUpdateRequestLogUpdateRequestListOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .build();
    }

    public AnnualDutyResponse.UpdateAnnualDutyOutDTO toUpdateAnnualDutyOutDTO() {
        return AnnualDutyResponse.UpdateAnnualDutyOutDTO
                .builder()
                .id(id)
                .annualDuty(annualDuty.toAnnualDutyUpdateAnnualDutyOutDTO())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .build();
    }
}
