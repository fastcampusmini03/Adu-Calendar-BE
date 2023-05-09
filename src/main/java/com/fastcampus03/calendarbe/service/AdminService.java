package com.fastcampus03.calendarbe.service;

import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception400;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.core.exception.Exception500;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.admin.AdminRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyChecked;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyCheckedRepository;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import com.fastcampus03.calendarbe.util.StatusConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Transactional
    public ResponseDTO<?> 일정등록요청승인(Long saveId, MyUserDetails myUserDetails) {
        // 권한의 경우 수정이 가능한 영역이기 때문에 토큰에 있는 권한과 실제 DB에 있는 권한이 다를 수 있음
        // 토큰에 있는 ID가 현재 존재하는 관리자인지 확인
        // DB 조회 후 권한 검증
        인증(myUserDetails);

        AnnualDuty saveAnnualDutyPS = annualDutyRepository.findByUserId(saveId)
                .orElseThrow(() -> new Exception400("id", "등록 승인하려는 일정이 존재하지 않습니다."));
        try {
            saveAnnualDutyPS.approvedStatus(); // 승인됨(1)로
            // 유저에게 일정 확인을 전달해주는 로그 기록
            annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                    .isShown(false)
                    .annualDuty(saveAnnualDutyPS)
                    .msg(StatusConst.ANNUALDUTY_APPROVED)
                    .build());
            return new ResponseDTO<>(saveAnnualDutyPS);
        }catch (Exception e) {
            throw new Exception500("일정 등록에 실패했습니다.");
        }
    }

    @Transactional
    public ResponseDTO<?> 일정등록요청거절(Long saveId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        AnnualDuty saveAnnualDutyPS = annualDutyRepository.findByUserId(saveId)
                .orElseThrow(() -> new Exception400("id", "등록 거절하려는 일정이 존재하지 않습니다. "));
        try {
            saveAnnualDutyPS.rejectedStatus();
            // 유저에게 일정 확인을 전달해주는 로그 기록
            annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                    .isShown(false)
                    .annualDuty(saveAnnualDutyPS)
                    .msg(StatusConst.ANNUALDUTY_REJECTED)
                    .build());
            return new ResponseDTO<>(null);
        }catch (Exception e) {
            throw new Exception500("일정 등록에 실패했습니다.");
        }
    }

    @Transactional
    public ResponseDTO<?> 삭제요청승인(Long deleteId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        AnnualDuty deleteAnnualDutyPS = annualDutyRepository.findByUserId(deleteId)
                .orElseThrow(() -> new Exception400("id", "삭제하려는 일정이 존재하지 않습니다. "));
        try {
            deleteAnnualDutyPS.rejectedStatus(); // 거절됨(2)로
            deleteAnnualDutyPS.afterRequestProcess();
            // 유저에게 일정 확인을 전달해주는 로그 기록
            annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                    .isShown(false)
                    .annualDuty(deleteAnnualDutyPS)
                    .msg(StatusConst.ANNUALDUTY_APPROVED)
                    .build());
            return new ResponseDTO<>(null);
        }catch (Exception e) {
            throw new Exception500("일정 삭제에 실패했습니다.");
        }
    }

    @Transactional
    public ResponseDTO<?> 삭제요청거절(Long deleteId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        AnnualDuty deleteAnnualDutyPS = annualDutyRepository.findByUserId(deleteId)
                .orElseThrow(() -> new Exception400("id", "삭제하려는 일정이 존재하지 않습니다. "));
        try {
            deleteAnnualDutyPS.afterRequestProcess();
            // 유저에게 일정 확인을 전달해주는 로그 기록
            annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                    .isShown(false)
                    .annualDuty(deleteAnnualDutyPS)
                    .msg(StatusConst.ANNUALDUTY_REJECTED)
                    .build());
            return new ResponseDTO<>(null);
        }catch (Exception e) {
            throw new Exception500("일정 삭제에 실패했습니다. ");
        }
    }

    @Transactional
    public ResponseDTO<?> 수정요청승인(Long updateId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.findById(updateId)
                .orElseThrow(() -> new Exception400("id", "수정사항이 존재하지 않습니다. "));

        try {
            AnnualDuty updateAnnualDutyPS = updateRequestLogPS.getAnnualDuty();
            String title = updateRequestLogPS.getTitle();
            LocalDateTime startTime = updateRequestLogPS.getStartTime();
            LocalDateTime endTime = updateRequestLogPS.getEndTime();
            updateRequestLogPS.setStatus(true); // 처리 완료
            updateAnnualDutyPS.updateAnnualDuty(title, startTime, endTime);
            updateAnnualDutyPS.afterRequestProcess();
            // 유저에게 일정 확인을 전달해주는 로그 기록
            annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                    .isShown(false)
                    .annualDuty(updateAnnualDutyPS)
                    .msg(StatusConst.ANNUALDUTY_APPROVED)
                    .build());
            return new ResponseDTO<>(updateAnnualDutyPS);
        } catch (Exception e) {
            throw new Exception500("일정 수정에 실패했습니다. ");
        }
    }

    @Transactional
    public ResponseDTO<?> 수정요청거절(Long updateId, MyUserDetails myUserDetails) {
        인증(myUserDetails);

        UpdateRequestLog updateRequestLogPS = updateRequestLogRepository.findById(updateId)
                .orElseThrow(() -> new Exception400("id", "수정사항이 존재하지 않습니다. "));
        try {
            AnnualDuty updateAnnualDutyPS = updateRequestLogPS.getAnnualDuty();
            updateRequestLogPS.setStatus(true);
            updateAnnualDutyPS.afterRequestProcess();
            // 유저에게 일정 확인을 전달해주는 로그 기록
            annualDutyCheckedRepository.save(AnnualDutyChecked.builder()
                    .isShown(false)
                    .annualDuty(updateAnnualDutyPS)
                    .msg(StatusConst.ANNUALDUTY_REJECTED)
                    .build());
            return new ResponseDTO<>(null);
        } catch (Exception e) {
            throw new Exception500("일정 수정에 실패했습니다. ");
        }
    }

    public ResponseDTO<?> 승인요청데이터조회(MyUserDetails myUserDetails) {
        인증(myUserDetails);

        List<AnnualDuty> annualDutyListPS = annualDutyRepository.findAllByStatus(StatusConst.APPROVING);
        return new ResponseDTO<>(annualDutyListPS);
    }

    public ResponseDTO<?> 수정요청데이터조회(MyUserDetails myUserDetails) {
        인증(myUserDetails);

        List<UpdateRequestLog> updateRequestLogListPS = updateRequestLogRepository.findAllByStatus(false);
        return new ResponseDTO<>(updateRequestLogListPS);
    }

    public ResponseDTO<?> 삭제요청데이터조회(MyUserDetails myUserDetails) {
        인증(myUserDetails);

        List<AnnualDuty> annualDutyListPS = annualDutyRepository.findAllByUpdateStatus(StatusConst.UPDATE_DELETESTATUS);
        return new ResponseDTO<>(annualDutyListPS);
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
    public ResponseDTO<?> 유저권한수정(AdminRequest.UpdateRoleDTO updateRoleDTO) {
        User userPS = userRepository.findByEmail(updateRoleDTO.getEmail())
                .orElseThrow(() -> new Exception400("email", "등록되지 않은 유저입니다. "));
        userPS.updateRole(updateRoleDTO.getRole());
        return new ResponseDTO<>(userPS);
    }
}