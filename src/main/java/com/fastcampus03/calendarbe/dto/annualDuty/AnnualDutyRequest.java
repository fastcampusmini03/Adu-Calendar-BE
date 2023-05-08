package com.fastcampus03.calendarbe.dto.annualDuty;

import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class AnnualDutyRequest {
    @Setter
    @Getter
    @Builder
    public static class SaveInDTO {

        @NotEmpty
        @Size(min = 1, max = 5)
        private String calendarId;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,10}$", message = "한글/영문 1~10자 이내로 작성해주세요")
        private String username;

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,100}$", message = "한글/영문 1~100자 이내로 작성해주세요")
        private String title;

        @NotEmpty
        private LocalDateTime start;

        @NotEmpty
        private LocalDateTime end;

        @NotEmpty
        private Boolean isAllday; // false: 당직, true: 연차

        @AssertTrue(message = "End time should be after start time")
        public boolean isEndTimeAfterStartTime() {
            if (start == null || end == null) {
                return true;
            }
            return end.isAfter(start);
        }

        @NotEmpty
        private String role;

        public AnnualDuty toEntity() {
            return AnnualDuty.builder()
                    .status(calendarId)
                    .title(title)
                    .startTime(start)
                    .endTime(end)
                    .type(isAllday)
                    .build();
            // user는 save할 때 ah에서 꺼내서 넣어준다
        }
    }

        @Setter
        @Getter
        @Builder
        public static class UpdateInDTO{

            @NotEmpty
            @Pattern(regexp = "^[a-zA-Z가-힣]{1,100}$", message = "한글/영문 1~100자 이내로 작성해주세요")
            private String title;

            @NotEmpty
            private LocalDateTime start;

            @NotEmpty
            private LocalDateTime end;

            @NotEmpty
            private Long annualDutyId;

        }

        @Setter
        @Getter
        @Builder
        public static class DeleteDTO{

            @NotEmpty
            private Long deleteId;

        }
}
