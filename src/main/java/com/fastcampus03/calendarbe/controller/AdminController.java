package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.admin.AdminRequest;
import com.fastcampus03.calendarbe.dto.admin.AdminResponse;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/s/admin/update/role")
    public ResponseEntity<?> updateRole(
            @RequestBody AdminRequest.UpdateRoleDTO updateRoleDTO,
            @AuthenticationPrincipal MyUserDetails myUserDetails){
        User user = adminService.유저권한수정(updateRoleDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(user.toUpdateRoleOutDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/save/accept/{id}")
    public ResponseEntity<?> acceptSave(
            @PathVariable("id") Long saveId,
            @AuthenticationPrincipal MyUserDetails myUserDetails){
        AnnualDuty annualDuty = adminService.일정등록요청승인(saveId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDuty.toAcceptSaveOutDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/save/reject/{id}")
    public ResponseEntity<?> rejectSave(
            @PathVariable("id") Long saveId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        adminService.일정등록요청거절(saveId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/delete/accept/{id}")
    public ResponseEntity<?> acceptDelete(
            @PathVariable("id") Long deleteId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        adminService.삭제요청승인(deleteId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/delete/reject/{id}")
    public ResponseEntity<?> rejectDelete(
            @PathVariable("id") Long deleteId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        adminService.삭제요청거절(deleteId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/update/accept/{id}")
    public ResponseEntity<?> acceptUpdate(
            @PathVariable("id") Long updateId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        AnnualDuty annualDuty = adminService.수정요청승인(updateId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDuty.toAcceptUpdateOutDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/update/reject/{id}")
    public ResponseEntity<?> rejectUpdate(
            @PathVariable("id") Long updateId,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ){
        adminService.수정요청거절(updateId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/save")
    public ResponseEntity<?> saveRequestList(
            @RequestParam(defaultValue ="0") Integer page,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Page<AnnualDuty> annualDutyPage = adminService.승인요청데이터조회(page, myUserDetails);
        Page<AdminResponse.AnnualDutySaveRequestListOutDTO> annualDutySaveRequestListOutDTOPage
                = annualDutyPage.map(o -> o.toAnnualDutySaveRequestListOutDTO());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDutySaveRequestListOutDTOPage);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/update")
    public ResponseEntity<?> updateRequestList(
            @RequestParam(defaultValue ="0") Integer page,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Page<UpdateRequestLog> updateRequestLogPage = adminService.수정요청데이터조회(page, myUserDetails);
        Page<AdminResponse.UpdateRequestLogUpdateRequestListOutDTO> updateRequestLogUpdateRequestListOutDTOPage
                = updateRequestLogPage.map(o -> o.toUpdateRequestLogUpdateRequestListOutDTO());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateRequestLogUpdateRequestListOutDTOPage);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/delete")
    public ResponseEntity<?> deleteRequestList(
            @RequestParam(defaultValue ="0") Integer page,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Page<AnnualDuty> annualDutyPage = adminService.삭제요청데이터조회(page, myUserDetails);
        Page<AdminResponse.AnnualDutyDeleteRequestListOutDTO> annualDutyDeleteRequestListOutDTOPage
                = annualDutyPage.map(o -> o.toAnnualDutyDeleteRequestListOutDTO());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDutyDeleteRequestListOutDTOPage);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/admin/users")
    public ResponseEntity<?> usersList(
            @RequestParam(defaultValue ="0") Integer page,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Page<User> userPage = adminService.전체유저조회(page, myUserDetails);
        Page<AdminResponse.UserUsersListOutDTO> userUsersListPage = userPage.map(o -> o.toUserUsersList());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(userUsersListPage);
        return ResponseEntity.ok().body(responseDTO);
    }
}
