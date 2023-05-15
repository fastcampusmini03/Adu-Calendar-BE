package com.fastcampus03.calendarbe.dto.admin;

import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class AdminResponse {

    @Getter
    @Setter
    @Builder
    public static class UpdateRoleOutDTO{
        private Long id;
        private String email;
        private String username;
        private String role;
    }

    @Getter
    @Setter
    @Builder
    public static class AcceptSaveOutDTO{
        private Long id;
        private String status;
        private UserAcceptSaveOutDTO user;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean type;
        private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

        @Getter
        @Setter
        @Builder
        public static class UserAcceptSaveOutDTO {
            private Long id;
            private String email;
            private String username;
            private String role;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class AcceptUpdateOutDTO{
        private Long id;
        private String status;
        private UserAcceptUpdateOutDTO user;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean type;
        private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

        @Getter
        @Setter
        @Builder
        public static class UserAcceptUpdateOutDTO {
            private Long id;
            private String email;
            private String username;
            private String role;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class AnnualDutySaveRequestListOutDTO {
        private Long id;
        private String status;
        private UserAnnualDutySaveRequestListOutDTO user;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean type;
        private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

        @Getter
        @Setter
        @Builder
        public static class UserAnnualDutySaveRequestListOutDTO {
            private Long id;
            private String email;
            private String username;
            private String role;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class AnnualDutyDeleteRequestListOutDTO {
        private Long id;
        private String status;
        private UserAnnualDutyDeleteRequestListOutDTO user;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean type;
        private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

        @Getter
        @Setter
        @Builder
        public static class UserAnnualDutyDeleteRequestListOutDTO {
            private Long id;
            private String email;
            private String username;
            private String role;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateRequestLogUpdateRequestListOutDTO {
        private Long id;
        private AnnualDutyUpdateRequestLogUpdateRequestListOutDTO annualDuty;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean status;

        @Getter
        @Setter
        @Builder
        public static class AnnualDutyUpdateRequestLogUpdateRequestListOutDTO {
            private Long id;
            private String status;
            private UserAnnualDutyUpdateRequestLogUpdateRequestListOutDTO user;
            private String title;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private Boolean type;
            private Integer updateStatus; // null: default, 1: 업데이트 요청, 2: 삭제 요청

            @Getter
            @Setter
            @Builder
            public static class UserAnnualDutyUpdateRequestLogUpdateRequestListOutDTO {
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
    public static class UserUsersListOutDTO {
        private Long id;
        private String email;
        private String username;
        private String role;
    }
}
