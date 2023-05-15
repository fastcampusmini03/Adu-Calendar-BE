package com.fastcampus03.calendarbe.service;

import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception400;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.core.exception.Exception500;
import com.fastcampus03.calendarbe.core.util.StatusConst;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.admin.AdminRequest;
import com.fastcampus03.calendarbe.model.annualDuty.*;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final AnnualDutyRepository annualDutyRepository;
    private final UpdateRequestLogRepository updateRequestLogRepository;
    private final AnnualDutyCheckedRepository annualDutyCheckedRepository;

    private final AnnualDutyQueryRepository annualDutyQueryRepository;
    @Transactional
    public AnnualDuty 일정등록요청승인(Long saveId, MyUserDetails myUserDetails) {
        // 권한의 경우 수정이 가능한 영역이기 때문에 토큰에 있는 권한과 실제 DB에 있는 권한이 다를 수 있음
        // 토큰에 있는 ID가 현재 존재하는 관리자인지 확인
        // DB 조회 후 권한 검증
        인증(myUserDetails);

        AnnualDuty annualDutyPS = annualDutyRepository.findByUserId(saveId)
                .orElseThrow(() -> new Exception400("id", "등록 승인하려는 일정이 존재하지 않습니다."));
        annualDutyPS.approvedStatus(); // 승인됨(1)로
        // 유저에게 일정 확인을 전달해주는 로그 기록
        annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                .isShown(false)
                .annualDuty(annualDutyPS)
                .msg(StatusConst.ANNUALDUTY_APPROVED)
                .build());
        return annualDutyPS;
    }

    @Transactional
    public void 일정등록요청거절(Long saveId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        AnnualDuty saveAnnualDutyPS = annualDutyRepository.findByUserId(saveId)
                .orElseThrow(() -> new Exception400("id", "등록 거절하려는 일정이 존재하지 않습니다. "));
        saveAnnualDutyPS.rejectedStatus();
        // 유저에게 일정 확인을 전달해주는 로그 기록
        annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                .isShown(false)
                .annualDuty(saveAnnualDutyPS)
                .msg(StatusConst.ANNUALDUTY_REJECTED)
                .build());
    }

    @Transactional
    public void 삭제요청승인(Long deleteId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        AnnualDuty deleteAnnualDutyPS = annualDutyRepository.findByUserId(deleteId)
                .orElseThrow(() -> new Exception400("id", "삭제하려는 일정이 존재하지 않습니다. "));
        deleteAnnualDutyPS.rejectedStatus(); // 거절됨(2)로
        deleteAnnualDutyPS.afterRequestProcess();
        // 유저에게 일정 확인을 전달해주는 로그 기록
        annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                .isShown(false)
                .annualDuty(deleteAnnualDutyPS)
                .msg(StatusConst.ANNUALDUTY_DELETE_APPROVED)
                .build());
    }

    @Transactional
    public void 삭제요청거절(Long deleteId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        AnnualDuty deleteAnnualDutyPS = annualDutyRepository.findByUserId(deleteId)
                .orElseThrow(() -> new Exception400("id", "삭제하려는 일정이 존재하지 않습니다. "));
        deleteAnnualDutyPS.afterRequestProcess();
        // 유저에게 일정 확인을 전달해주는 로그 기록
        annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                .isShown(false)
                .annualDuty(deleteAnnualDutyPS)
                .msg(StatusConst.ANNUALDUTY_DELETE_REJECTED)
                .build());
    }

    @Transactional
    public AnnualDuty 수정요청승인(Long updateId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.findById(updateId)
                .orElseThrow(() -> new Exception400("id", "수정사항이 존재하지 않습니다. "));

        AnnualDuty annualDutyPS = updateRequestLogPS.getAnnualDuty();
        String title = updateRequestLogPS.getTitle();
        LocalDateTime startTime = updateRequestLogPS.getStartTime();
        LocalDateTime endTime = updateRequestLogPS.getEndTime();
        updateRequestLogPS.setStatus(true); // 처리 완료
        annualDutyPS.updateAnnualDuty(title, startTime, endTime);
        annualDutyPS.afterRequestProcess();
        // 유저에게 일정 확인을 전달해주는 로그 기록
        annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                .isShown(false)
                .annualDuty(annualDutyPS)
                .msg(StatusConst.ANNUALDUTY_UPDATE_APPROVED)
                .build());
        return annualDutyPS;
    }

    @Transactional
    public void 수정요청거절(Long updateId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.findById(updateId)
                .orElseThrow(() -> new Exception400("id", "수정사항이 존재하지 않습니다. "));
        AnnualDuty updateAnnualDutyPS = updateRequestLogPS.getAnnualDuty();
        updateRequestLogPS.setStatus(true);
        updateAnnualDutyPS.afterRequestProcess();
        // 유저에게 일정 확인을 전달해주는 로그 기록
        annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                .isShown(false)
                .annualDuty(updateAnnualDutyPS)
                .msg(StatusConst.ANNUALDUTY_UPDATE_REJECTED)
                .build());
    }

    public Page<AnnualDuty> 승인요청데이터조회(Integer page, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        return annualDutyQueryRepository.findAllByStatus(page, StatusConst.APPROVING);
    }

    public Page<UpdateRequestLog> 수정요청데이터조회(Integer page, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        return annualDutyQueryRepository.findAllByUpdateLogStatus(page, false);
    }

    public Page<AnnualDuty> 삭제요청데이터조회(Integer page, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        return annualDutyQueryRepository.findAllByUpdateStatus(page, StatusConst.UPDATE_DELETESTATUS);
    }

    private void 인증(MyUserDetails myUserDetails) {
        User user = myUserDetails.getUser();

        User userPS = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception400("id", "등록되지 않은 유저입니다."));

        if (!userPS.getRole().equals(user.getRole())) {
            throw new Exception401("권한이 부여되지 않았습니다.");
        }
    }

    @Transactional
    public User 유저권한수정(AdminRequest.UpdateRoleDTO updateRoleDTO, MyUserDetails myUserDetails) {
        인증(myUserDetails);
        User userPS = userRepository.findByEmail(updateRoleDTO.getEmail())
                .orElseThrow(() -> new Exception400("email", "등록되지 않은 유저입니다. "));
        userPS.updateRole(updateRoleDTO.getRole());
        return userPS;
    }

    public Page<User> 전체유저조회(Integer page, MyUserDetails myUserDetails) {
        인증(myUserDetails);
        return annualDutyQueryRepository.findAllUser(page);
    }
}
