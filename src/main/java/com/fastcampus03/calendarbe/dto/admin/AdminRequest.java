package com.fastcampus03.calendarbe.dto.admin;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class AdminRequest {

    @Getter
    public static class UpdateRoleDTO{

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @Pattern(regexp = "USER|ADMIN")
        private String role;
    }
}
