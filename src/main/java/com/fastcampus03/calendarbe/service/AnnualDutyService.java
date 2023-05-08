package com.fastcampus03.calendarbe.service;

import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception400;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.core.exception.Exception500;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyAcceptedRepository;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnualDutyService {
    private final UserRepository userRepository;
    private final AnnualDutyRepository annualDutyRepository;
    private final AnnualDutyAcceptedRepository annualDutyAcceptedRepository;
    private final UpdateRequestLogRepository updateLogRepository;
    @Transactional
    public AnnualDuty 일정등록(AnnualDutyRequest.SaveInDTO saveDTO) {
        User userPS = userRepository.findByEmail(saveDTO.getEmail())
                .orElseThrow(() -> new Exception400("email", "등록되지 않은 유저입니다. "));
        try {
            AnnualDuty annualDuty = saveDTO.toEntity(userPS);
            AnnualDuty annualDutyPS = annualDutyRepository.save(annualDuty); // calendarID(status) = 0
            return annualDutyPS;
        } catch (Exception e) {
            throw new Exception500("일정 등록에 실패하였습니다. "  + e.getMessage());
        }
    }

    @Transactional
    public UpdateRequestLog 일정수정(AnnualDutyRequest.UpdateInDTO updateInDTO, MyUserDetails myUserDetails) {
        AnnualDuty prevAnnualDutyPS = annualDutyRepository.findById(updateInDTO.getAnnualDutyId()) // 바로 entity로 받을 수 있으면 받기
                .orElseThrow(() -> new Exception400("id", "수정하려는 일정이 존재하지 않습니다. "));

        if(!myUserDetails.getUser().getEmail().equals(prevAnnualDutyPS.getUser().getEmail())){
            throw new Exception401("본인의 일정만 수정할 수 있습니다. ");
        }

        try {
            UpdateRequestLog updateRequestLog = updateInDTO.toEntity();
            updateRequestLog.setAnnualDuty(prevAnnualDutyPS);

            prevAnnualDutyPS.setUpdateStatus(1); //null -> 1
            UpdateRequestLog updateRequestLogPS = updateLogRepository.save(updateRequestLog);
            return updateRequestLogPS;
        } catch (Exception e) {
            throw new Exception500("일정 수정 요청에 실패하였습니다. "  + e.getMessage());
        }
    }

    @Transactional
    public AnnualDuty 일정삭제(AnnualDutyRequest.DeleteInDTO deleteInDTO, MyUserDetails myUserDetails) {
        if(!myUserDetails.getUser().getId().equals(deleteInDTO.getDeleteId())){
            throw new Exception401("본인의 일정만 삭제할 수 있습니다. ");
        }
        try {
            AnnualDuty deleteAnnualdutyPS = annualDutyRepository.findById(deleteInDTO.getDeleteId())
                    .orElseThrow(() -> new Exception400("email", "삭제하려는 일정이 존재하지 않습니다. "));
            deleteAnnualdutyPS.setUpdateStatus(2); // null -> 2
            return deleteAnnualdutyPS;
        } catch (Exception400 e) {
            throw new Exception500("일정 삭제 요청에 실패하였습니다. "  + e.getMessage());
        }
    }

//    public List<AnnualDuty> 전체일정조회(){}
//
//    public List<AnnualDutyAccepted> 회원별일정조회(Long id){
//
//    }



}
