package com.fastcampus03.calendarbe.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception400;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.core.exception.Exception500;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.dto.annualDuty.AnnualDutyRequest;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyQueryRepository;
import com.fastcampus03.calendarbe.model.annualDuty.AnnualDutyRepository;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLogRepository;
import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnualDutyService {
    private final UserRepository userRepository;
    private final AnnualDutyRepository annualDutyRepository;
    private final AnnualDutyQueryRepository annualDutyQueryRepository;
    private final UpdateRequestLogRepository updateLogRepository;

    @Transactional
    public AnnualDuty 일정등록(AnnualDutyRequest.SaveInDTO saveDTO) {
        // 리팩토링 요소로 남겨둔다.
        User userPS = userRepository.findByEmail(saveDTO.getEmail())
                .orElseThrow(() -> new Exception400("email", "등록되지 않은 유저입니다."));
        return annualDutyRepository.save(saveDTO.toEntity(userPS));
    }

    @Transactional
    public UpdateRequestLog 일정수정(Long id, AnnualDutyRequest.UpdateInDTO updateInDTO, MyUserDetails myUserDetails) {
        User user = myUserDetails.getUser();

        AnnualDuty prevAnnualDuty = annualDutyRepository.findById(id)
                .orElseThrow(() -> new Exception400("id", "존재하지 않는 일정입니다."));

        // jwt 토큰의 id 존재 유무를 확인할 수 있다.
        if(!user.getId().equals(prevAnnualDuty.getUser().getId())){
            throw new Exception401("본인의 일정만 수정할 수 있습니다.");
        }

        prevAnnualDuty.updateRequest();
        return updateLogRepository.save(updateInDTO.toEntity(prevAnnualDuty));
    }

    @Transactional
    public void 일정삭제(Long id, MyUserDetails myUserDetails) {
        User user = myUserDetails.getUser();

        AnnualDuty deleteAnnualdutyPS = annualDutyRepository.findById(id)
                .orElseThrow(() -> new Exception400("id", "존재하지 않는 일정입니다."));

        // jwt 토큰의 id 존재 유무를 확인할 수 있다.
        if(!user.getId().equals(deleteAnnualdutyPS.getUser().getId())){
            throw new Exception401("본인의 일정만 수정할 수 있습니다.");
        }

        deleteAnnualdutyPS.deleteRequest(); // null -> 2
    }

    public List<AnnualDuty> 일정조회(LocalDateTime startDate, LocalDateTime endDate, MyUserDetails myUserDetails) {
        User user = 인증(myUserDetails);
        return annualDutyQueryRepository.findByDateRange(startDate, endDate, user);
    }

    private User 인증(MyUserDetails myUserDetails) {

        if (myUserDetails != null) {
            User user = myUserDetails.getUser();
            User userPS = userRepository.findById(user.getId())
                    .orElseThrow(() -> new Exception400("id", "등록되지 않은 유저입니다."));

            if (!userPS.getRole().equals(user.getRole())) {
                throw new Exception401("토큰이 유효하지 않습니다.");
            }
            return userPS;
        } else {
            return null;
        }
    }
}
