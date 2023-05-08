package com.fastcampus03.calendarbe.model.annualDuty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnualDutyCheckedRepository extends JpaRepository<AnnualDutyChecked, Long> {

//    @Query("select adc from AnnualDutyChecked adc where adc.isShown=:is_shown and adc.")
//    List<AnnualDutyChecked> findAllBySaveList(boolean is_shown);
}
