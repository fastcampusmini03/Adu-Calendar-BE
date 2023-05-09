package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.admin.AdminRequest;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/s/admin/update/role")
    public ResponseEntity<?> updateRole(
            @RequestBody AdminRequest.UpdateRoleDTO updateRoleDTO){
        ResponseDTO<?> responseDTO = adminService.유저권한수정(updateRoleDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/save/accept/{id}")
    public ResponseEntity<?> acceptSave(
            @PathVariable("id") Long saveId,
            @AuthenticationPrincipal MyUserDetails myUserDetails){
        ResponseDTO<?> responseDTO = adminService.일정등록요청승인(saveId, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/save/reject/{id}")
    public ResponseEntity<?> rejectSave(
            @PathVariable("id") Long saveId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        ResponseDTO<?> responseDTO = adminService.일정등록요청거절(saveId, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/delete/accept/{id}")
    public ResponseEntity<?> acceptDelete(
            @PathVariable("id") Long deleteId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        ResponseDTO<?> responseDTO = adminService.삭제요청승인(deleteId, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/delete/reject/{id}")
    public ResponseEntity<?> rejectDelete(
            @PathVariable("id") Long deleteId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        ResponseDTO<?> responseDTO = adminService.삭제요청거절(deleteId, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/update/accept/{id}")
    public ResponseEntity<?> acceptUpdate(
            @PathVariable("id") Long updateId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        ResponseDTO<?> responseDTO = adminService.수정요청승인(updateId, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/update/reject/{id}")
    public ResponseEntity<?> rejectUpdate(
            @PathVariable("id") Long updateId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        ResponseDTO<?> responseDTO = adminService.수정요청거절(updateId, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/save")
    public ResponseEntity<?> saveRequestList(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = adminService.승인요청데이터조회(myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/update")
    public ResponseEntity<?> updateRequestList(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = adminService.수정요청데이터조회(myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/delete")
    public ResponseEntity<?> deleteRequestList(@AuthenticationPrincipal MyUserDetails myUserDetails) {
            ResponseDTO<?> responseDTO = adminService.삭제요청데이터조회(myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }
}
