package com.fastcampus03.calendarbe.service;

import com.fastcampus03.calendarbe.core.exception.Exception400;
import com.fastcampus03.calendarbe.core.exception.Exception500;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AnnualDutyRepository annualDutyRepository;

    private final UpdateRequestLogRepository updateRequestLogRepository;

    @Transactional
    public AnnualDuty 일정등록요청승인(Long saveId) {
        AnnualDuty saveAnnualDutyPS = annualDutyRepository.findById(saveId)
                .orElseThrow(() -> new Exception400("id", "등록 승인하려는 일정이 존재하지 않습니다. "));
        try {
            saveAnnualDutyPS.setStatus("1"); // 승인됨(1)로
            // 유저가 아직 확인 안한 거로
            return saveAnnualDutyPS;
        }catch (Exception e) {
            throw new Exception500("일정 승인에 실패했습니다. ");
        }
    }

    @Transactional
    public AnnualDuty 일정등록요청거절(Long saveId) {
        AnnualDuty saveAnnualDutyPS = annualDutyRepository.findById(saveId)
                .orElseThrow(() -> new Exception400("id", "등록 거절하려는 일정이 존재하지 않습니다. "));
        try {
            saveAnnualDutyPS.setStatus("2"); // 거절됨(2)로
            // 유저가 아직 확인 안한 거로
            return saveAnnualDutyPS;
        }catch (Exception e) {
            throw new Exception500("일정 승인에 실패했습니다. ");
        }
    }

    @Transactional
    public AnnualDuty 삭제요청승인(Long deleteId) {
        AnnualDuty deleteAnnualDutyPS = annualDutyRepository.findById(deleteId)
                .orElseThrow(() -> new Exception400("id", "삭제하려는 일정이 존재하지 않습니다. "));
        try {
            deleteAnnualDutyPS.setStatus("2"); // 거절됨(2)로
            deleteAnnualDutyPS.setUpdateStatus(null);
            // 유저가 아직 확인 안한 거로
            return deleteAnnualDutyPS;
        }catch (Exception e) {
            throw new Exception500("일정 삭제에 실패했습니다. ");
        }
    }

    @Transactional
    public AnnualDuty 삭제요청거절(Long deleteId) {
        AnnualDuty deleteAnnualDutyPS = annualDutyRepository.findById(deleteId)
                .orElseThrow(() -> new Exception400("id", "삭제하려는 일정이 존재하지 않습니다. "));
        try {
            deleteAnnualDutyPS.setUpdateStatus(null); // 삭제요청중 -> null으로
            // 유저가 아직 확인 안한 거로
            return deleteAnnualDutyPS;
        }catch (Exception e) {
            throw new Exception500("일정 삭제에 실패했습니다. ");
        }
    }

    @Transactional
    public AnnualDuty 수정요청승인(Long updateId) {
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.findById(updateId)
                .orElseThrow(() -> new Exception400("id", "수정사항이 존재하지 않습니다. "));

        AnnualDuty updateAnnualDutyPS = updateRequestLogPS.getAnnualDuty();
        updateAnnualDutyPS.setStartTime(updateRequestLogPS.getStartTime());
        updateAnnualDutyPS.setEndTime(updateRequestLogPS.getEndTime());
        updateAnnualDutyPS.setTitle(updateRequestLogPS.getTitle());
        updateAnnualDutyPS.setUpdateStatus(null);
        // 유저가 아직 확인 안한 거로
        updateRequestLogPS.setStatus(true); // 처리 완료
        return updateAnnualDutyPS;
    }

    public AnnualDuty 수정요청거절(Long updateId) {
        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.findById(updateId)
                .orElseThrow(() -> new Exception400("id", "수정사항이 존재하지 않습니다. "));
        AnnualDuty updateAnnualDutyPS = updateRequestLogPS.getAnnualDuty();
        updateAnnualDutyPS.setUpdateStatus(null);
        // 유저가 아직 확인 안한 거로
        updateRequestLogPS.setStatus(true);
        return updateAnnualDutyPS;
    }
}
