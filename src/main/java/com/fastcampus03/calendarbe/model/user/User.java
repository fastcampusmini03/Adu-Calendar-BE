package com.fastcampus03.calendarbe.model.user;

import com.fastcampus03.calendarbe.core.util.TimeBaseEntity;
import com.fastcampus03.calendarbe.dto.admin.AdminResponse;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyResponse;
import com.fastcampus03.calendarbe.dto.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "user_tb")
@Entity
public class User extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Column(nullable = false, length = 60) // 패스워드 인코딩(BCrypt)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, length = 20)
    private String username;

    private String role;

    private LocalDateTime loggedInAt;

    public void updateInfo(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public void updateRole(String newRole) {
        this.role = newRole;
    }

    public AdminResponse.UpdateRoleOutDTO toUpdateRoleOutDTO() {
        return AdminResponse.UpdateRoleOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AdminResponse.AcceptSaveOutDTO.UserAcceptSaveOutDTO toUserAcceptSaveOutDTO() {
        return AdminResponse.AcceptSaveOutDTO.UserAcceptSaveOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AdminResponse.AcceptUpdateOutDTO.UserAcceptUpdateOutDTO toUserAcceptUpdateOutDTO() {
        return AdminResponse.AcceptUpdateOutDTO.UserAcceptUpdateOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AdminResponse.AnnualDutySaveRequestListOutDTO.UserAnnualDutySaveRequestListOutDTO toUserAnnualDutySaveRequestListOutDTO() {
        return AdminResponse.AnnualDutySaveRequestListOutDTO.UserAnnualDutySaveRequestListOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AdminResponse.AnnualDutyDeleteRequestListOutDTO.UserAnnualDutyDeleteRequestListOutDTO toUserAnnualDutyDeleteRequestListOutDTO() {
        return AdminResponse.AnnualDutyDeleteRequestListOutDTO.UserAnnualDutyDeleteRequestListOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AdminResponse.UpdateRequestLogUpdateRequestListOutDTO.AnnualDutyUpdateRequestLogUpdateRequestListOutDTO.UserAnnualDutyUpdateRequestLogUpdateRequestListOutDTO toUserAnnualDutyUpdateRequestLogUpdateRequestListOutDTO() {
        return AdminResponse.UpdateRequestLogUpdateRequestListOutDTO.AnnualDutyUpdateRequestLogUpdateRequestListOutDTO.UserAnnualDutyUpdateRequestLogUpdateRequestListOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AdminResponse.UserUsersListOutDTO toUserUsersList() {
        return AdminResponse.UserUsersListOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public UserResponse.LoginOutDTO toLoinOutDTO() {
        return UserResponse.LoginOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public UserResponse.JoinOutDTO toJoinOutDTO() {
        return UserResponse.JoinOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public UserResponse.UserInfoDTO toUserInfoDTO() {
        return UserResponse.UserInfoDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public UserResponse.UpdateDTO toUpdateDTO() {
        return UserResponse.UpdateDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public UserResponse.AnnualDutyCheckOutDTO.AnnualDutyAnnualDutyCheckOutDTO.UserAnnualDutyAnnualDutyCheckOutDTO toUserAnnualDutyAnnualDutyCheckOutDTO() {
        return UserResponse.AnnualDutyCheckOutDTO.AnnualDutyAnnualDutyCheckOutDTO.UserAnnualDutyAnnualDutyCheckOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AnnualDutyResponse.SaveOutDTO.UserSaveOutDTO toUserSaveOutDTO() {
        return AnnualDutyResponse.SaveOutDTO.UserSaveOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AnnualDutyResponse.UpdateAnnualDutyOutDTO.AnnualDutyUpdateAnnualDutyOutDTO.UserAnnualDutyUpdateAnnualDutyOutDTO toUserAnnualDutyUpdateAnnualDutyOutDTO() {
        return AnnualDutyResponse.UpdateAnnualDutyOutDTO.AnnualDutyUpdateAnnualDutyOutDTO.UserAnnualDutyUpdateAnnualDutyOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

    public AnnualDutyResponse.ShowAnnualDutyListOutDTO.UserShowAnnualDutyListOutDTO toUserShowAnnualDutyListOutDTO() {
        return AnnualDutyResponse.ShowAnnualDutyListOutDTO.UserShowAnnualDutyListOutDTO
                .builder()
                .id(id)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }

}