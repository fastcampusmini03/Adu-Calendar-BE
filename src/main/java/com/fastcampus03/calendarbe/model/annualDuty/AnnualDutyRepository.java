package com.fastcampus03.calendarbe.model.annualDuty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnnualDutyRepository extends JpaRepository<AnnualDuty, Long> {

    @Query("select ad from AnnualDuty ad join fetch ad.user where ad.status=:status")
    List<AnnualDuty> findAllByStatus(String status);

    @Query("select ad from AnnualDuty ad join fetch ad.user where ad.updateStatus=:updateStatus")
    List<AnnualDuty> findAllByUpdateStatus(Integer updateStatus);

    @Query("select ad from AnnualDuty ad join fetch ad.user where ad.id=:id")
    Optional<AnnualDuty> findByUserId(Long id);


    @Query("select ad from AnnualDuty ad join fetch ad.user " +
            "where (ad.status='0' or ad.status='1') " +
            "and ((ad.startTime BETWEEN :startDate AND :endDate )" +
            "or (ad.endTime BETWEEN :startDate AND :endDate) " +
            "or (ad.startTime <= :endDate AND ad.endTime >= :startDate ))")
    List<AnnualDuty> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
