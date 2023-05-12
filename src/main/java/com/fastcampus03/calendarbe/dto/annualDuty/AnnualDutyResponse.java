package com.fastcampus03.calendarbe.dto.annualDuty;

import com.fastcampus03.calendarbe.dto.admin.AdminResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class AnnualDutyResponse {

    @Getter
    @Setter
    @Builder
    public static class SaveOutDTO {
        private Long id;
        private String status;
        private UserSaveOutDTO user;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean type;
        private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

        @Getter
        @Setter
        @Builder
        public static class UserSaveOutDTO {
            private Long id;
            private String email;
            private String username;
            private String role;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateAnnualDutyOutDTO {
        private Long id;
        private AnnualDutyUpdateAnnualDutyOutDTO annualDuty;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean status;

        @Getter
        @Setter
        @Builder
        public static class AnnualDutyUpdateAnnualDutyOutDTO {
            private Long id;
            private String status;
            private UserAnnualDutyUpdateAnnualDutyOutDTO user;
            private String title;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private Boolean type;
            private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

            @Getter
            @Setter
            @Builder
            public static class UserAnnualDutyUpdateAnnualDutyOutDTO {
                private Long id;
                private String email;
                private String username;
                private String role;
            }
        }
    }

    @Getter
    @Setter
    @Builder
    public static class ShowAnnualDutyListOutDTO {
        private Long id;
        private String status;
        private UserShowAnnualDutyListOutDTO user;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean type;
        private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

        @Getter
        @Setter
        @Builder
        public static class UserShowAnnualDutyListOutDTO {
            private Long id;
            private String email;
            private String username;
            private String role;
        }
    }
}
