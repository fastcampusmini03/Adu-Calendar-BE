package com.fastcampus03.calendarbe.model.annualDuty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnualDutyCheckedRepository extends JpaRepository<AnnualDutyChecked, Long> {

    @Query("select adcr from AnnualDutyChecked adcr where adcr.annualDuty.user.id=:id and adcr.isShown=:isShown")
    List<AnnualDutyChecked> findAllByIdAndIsShown(@Param("id") Long id, @Param("isShown") Boolean isShown);
}
