package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/s/admin/save/accept/{id}")
    public ResponseEntity<?> acceptSave(@PathVariable Long saveId){
        AnnualDuty savedAnnualDuty = adminService.일정등록요청승인(saveId);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(savedAnnualDuty);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/save/reject/{id}")
    public ResponseEntity<?> rejectSave(@PathVariable Long saveId){
        adminService.일정등록요청거절(saveId);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/delete/accept/{id}")
    public ResponseEntity<?> acceptDelete(@PathVariable Long deleteId){
        AnnualDuty deletedAnnualDuty = adminService.삭제요청승인(deleteId);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(deletedAnnualDuty);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/delete/reject/{id}")
    public ResponseEntity<?> rejectDelete(@PathVariable Long deleteId){
        AnnualDuty deletedAnnualDuty = adminService.삭제요청거절(deleteId);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(deletedAnnualDuty);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/update/accept/{id}")
    public ResponseEntity<?> acceptUpdate(@PathVariable Long updateId){
        AnnualDuty updatedAnnualDuty = adminService.수정요청승인(updateId);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(updatedAnnualDuty);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/admin/update/reject/{id}")
    public ResponseEntity<?> rejectUpdate(@PathVariable Long updateId){
        AnnualDuty updatedAnnualDuty = adminService.수정요청거절(updateId);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(updatedAnnualDuty);
        return ResponseEntity.ok().body(responseDTO);
    }
}
