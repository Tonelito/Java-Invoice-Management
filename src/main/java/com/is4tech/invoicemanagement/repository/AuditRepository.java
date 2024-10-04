package com.is4tech.invoicemanagement.repository;

import com.is4tech.invoicemanagement.model.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AuditRepository extends JpaRepository<Audit, Integer> {
    @Query("SELECT a FROM Audit a WHERE a.entity = :entity AND DATE(a.datetime) BETWEEN :startDate AND :endDate"
            + " AND (:userId IS NULL OR a.user.id = :userId)")
    Page<Audit> findByEntityAndDateRangeAndOptionalUserId(@Param("entity") String entity,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate,
                                                          @Param("userId") Integer userId,
                                                          Pageable pageable);

}
