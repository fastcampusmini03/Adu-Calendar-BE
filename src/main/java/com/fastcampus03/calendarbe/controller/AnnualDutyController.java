package com.fastcampus03.calendarbe.controller;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyResponse;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.service.AnnualDutyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        AnnualDuty annualDuty = annualDutyService.일정등록(saveInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(annualDuty.toSaveOutDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDuty/update/{id}")
    public ResponseEntity<?> updateAnnualDuty(
            @PathVariable("id") Long id,
            @RequestBody @Valid AnnualDutyRequest.UpdateInDTO updateInDTO,
            Errors errors,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        UpdateRequestLog updateRequestLog = annualDutyService.일정수정(id, updateInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateRequestLog.toUpdateAnnualDutyOutDTO());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/annualDuty/delete/{id}")
    public ResponseEntity<?> deleteAnnualDuty(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        annualDutyService.일정삭제(id, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/annualDuty")
    public ResponseEntity<?> showAnnualDutyList(@RequestParam("year") int year, @RequestParam("month") int month, HttpServletRequest request){
        LocalDateTime startDate = LocalDateTime.of(year, month-1, 16, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month+1, 15, 23, 59, 59);
        String prefixJwt = request.getHeader(MyJwtProvider.HEADER);

        List<AnnualDuty> annualDutyList = annualDutyService.일정조회(startDate, endDate, prefixJwt);
        List<AnnualDutyResponse.ShowAnnualDutyListOutDTO> showAnnualDutyListOutDTOList
                = annualDutyList.stream().map(o -> o.toShowAnnualDutyListOutDTO()).collect(Collectors.toList());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(showAnnualDutyListOutDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }

}
