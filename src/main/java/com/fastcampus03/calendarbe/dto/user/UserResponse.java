package com.fastcampus03.calendarbe.dto.user;

import com.fastcampus03.calendarbe.dto.admin.AdminResponse;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class UserResponse {

    @Getter
    @Setter
    @Builder
    public static class LoginOutDTO{
        private Long id;
        private String email;
        private String username;
        private String role;
    }

    @Getter
    @Setter
    @Builder
    public static class JoinOutDTO{
        private Long id;
        private String email;
        private String username;
        private String role;
    }

    @Getter
    @Setter
    @Builder
    public static class UserInfoDTO {
        private Long id;
        private String email;
        private String username;
        private String role;
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateDTO {
        private Long id;
        private String email;
        private String username;
        private String role;
    }

    @Getter
    @Setter
    @Builder
    public static class AnnualDutyCheckOutDTO {
        private Long id;
        private String username;
        private String msg;
        private AnnualDutyAnnualDutyCheckOutDTO annualDuty;

        @Getter
        @Setter
        @Builder
        public static class AnnualDutyAnnualDutyCheckOutDTO {
            private Long id;
            private String status;
            private UserAnnualDutyAnnualDutyCheckOutDTO user;
            private String title;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private Boolean type;
            private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

            @Getter
            @Setter
            @Builder
            public static class UserAnnualDutyAnnualDutyCheckOutDTO {
                private Long id;
                private String email;
                private String username;
                private String role;
            }
        }
    }

}
