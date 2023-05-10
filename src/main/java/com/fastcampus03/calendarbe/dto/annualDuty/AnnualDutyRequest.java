package com.fastcampus03.calendarbe.dto.annualDuty;

import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class AnnualDutyRequest {
    @Setter
    @Getter
    public static class SaveInDTO {

        @NotEmpty
        @Size(min = 1, max = 5)
        private String calendarId; // 0: 승인중 1: 승인됨 2: 거절

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,10}$", message = "한글/영문 1~10자 이내로 작성해주세요")
        private String username;

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Size(min = 1, max = 100)
        private String title;

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime start;

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime end;

        @NotNull
        private Boolean isAllday; // false: 당직, true: 연차

        @Pattern(regexp = "USER|ADMIN")
        private String role;

        @AssertTrue(message = "End time should be after start time")
        public boolean isEndTimeAfterStartTime() {
            if (start == null || end == null) {
                return true;
            }
            return end.isAfter(start);
        }

        public AnnualDuty toEntity(User user) {
            return AnnualDuty.builder()
                    .status(calendarId)
                    .title(title)
                    .startTime(start)
                    .endTime(end)
                    .user(user)
                    .type(isAllday)
                    .build();
        }
    }

        @Setter
        @Getter
        public static class UpdateInDTO{

            @NotEmpty
            @Size(min = 1, max = 100)
            private String title;

            @NotNull
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            private LocalDateTime start;

            @NotNull
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            private LocalDateTime end;

            @AssertTrue(message = "End time should be after start time")
            public boolean isEndTimeAfterStartTime() {
                if (start == null || end == null) {
                    return true;
                }
                return end.isAfter(start);
            }

            public UpdateRequestLog toEntity(AnnualDuty annualDuty){
                return UpdateRequestLog.builder()
                        .annualDuty(annualDuty)
                        .title(title)
                        .startTime(start)
                        .endTime(end)
                        .status(false)
                        .build();
            }

        }

}
