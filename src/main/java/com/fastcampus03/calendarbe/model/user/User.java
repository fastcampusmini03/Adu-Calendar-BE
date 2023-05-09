package com.fastcampus03.calendarbe.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    @Column(nullable = false, length = 60) // 패스워드 인코딩(BCrypt)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, length = 20)
    private String username;

    private String role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime loggedInAt;

    public void updateInfo(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public void updateRole(String newRole) {
        this.role = newRole;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}