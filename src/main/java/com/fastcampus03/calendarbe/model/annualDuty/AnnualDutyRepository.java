package com.fastcampus03.calendarbe.model.annualDuty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnualDutyRepository extends JpaRepository<AnnualDuty, Long> {

    @Query("select ad from AnnualDuty ad where ad.status=:status")
    List<AnnualDuty> findAllByStatus(String status);

    @Query("select ad from AnnualDuty ad where ad.updateStatus=:updateStatus")
    List<AnnualDuty> findAllByUpdateStatus(Integer updateStatus);
}
