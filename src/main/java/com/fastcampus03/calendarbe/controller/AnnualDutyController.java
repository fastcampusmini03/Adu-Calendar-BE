package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.service.AnnualDutyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnnualDutyController {

    private final AnnualDutyService annualDutyService;

    @PostMapping("/s/user/annualDuty/save")
    public ResponseEntity<?> save(
            @RequestBody @Valid AnnualDutyRequest.SaveInDTO saveInDTO,
            Errors errors
    ){
        ResponseDTO<?> responseDTO = annualDutyService.일정등록(saveInDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDuty/update/{id}")
    public ResponseEntity<?> updateAnnualDuty(
            @PathVariable Long id,
            @RequestBody @Valid AnnualDutyRequest.UpdateInDTO updateInDTO,
            Errors errors,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ResponseDTO<?> responseDTO = annualDutyService.일정수정(id, updateInDTO, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDuty/delete/{id}")
    public ResponseEntity<?> deleteAnnualDuty(
            @PathVariable Long id,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ResponseDTO<?> responseDTO = annualDutyService.일정삭제(id, myUserDetails);
        return ResponseEntity.ok().body(responseDTO);
    }

}
