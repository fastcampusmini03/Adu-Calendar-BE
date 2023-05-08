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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnnualDutyController {

    private final AnnualDutyService annualDutyService;

    @PostMapping("/s/user/annualDuty/save")
    public ResponseEntity<?> save(@RequestBody @Valid AnnualDutyRequest.SaveInDTO saveInDTO){

        AnnualDuty annualDuty = annualDutyService.일정등록(saveInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDuty); // 따로 responseDTO에 담을지
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDuty/update")
    public ResponseEntity< ? > updateAnnualDuty(
            @RequestBody AnnualDutyRequest.UpdateInDTO updateInDTO,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        UpdateRequestLog updateRequestLog = annualDutyService.일정수정(updateInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateRequestLog);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDuty/delete")
    public ResponseEntity< ? > deleteAnnualDuty(
            @RequestBody AnnualDutyRequest.DeleteInDTO deleteInDTO,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        AnnualDuty deleteAnnualDuty = annualDutyService.일정삭제(deleteInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(deleteAnnualDuty);
        return ResponseEntity.ok().body(responseDTO);
    }

}
