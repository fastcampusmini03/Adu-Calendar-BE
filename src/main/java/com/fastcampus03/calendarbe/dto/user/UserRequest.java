package com.fastcampus03.calendarbe.dto.user;

import com.fastcampus03.calendarbe.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRequest {
    @Setter
    @Getter
    @Builder
    public static class LoginInDTO {

        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty
        private String email;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;
    }

    @Setter
    @Getter
    public static class JoinInDTO {
        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .username(username)
                    .role("USER")
                    .build();
        }

        public UserRequest.LoginInDTO toLoginInDTO(String pw) {
            return UserRequest.LoginInDTO.builder()
                    .email(email)
                    .password(pw)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class UpdateInDTO {

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;
    }
}
