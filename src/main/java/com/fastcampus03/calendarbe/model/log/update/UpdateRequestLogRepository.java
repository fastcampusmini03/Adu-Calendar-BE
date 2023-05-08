package com.fastcampus03.calendarbe.model.log.update;

import com.fastcampus03.calendarbe.model.annualDuty.AnnualDuty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UpdateRequestLogRepository extends JpaRepository<UpdateRequestLog, Long> {

    @Query("select url from UpdateRequestLog url where url.status=:status")
    List<UpdateRequestLog> findAllByStatus(boolean status);
}
