package com.is4tech.invoicemanagement.repository;

import com.is4tech.invoicemanagement.model.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AuditRepository extends JpaRepository<Audit, Integer> {
    @Query("SELECT a FROM Audit a JOIN User u ON a.user.id = u.id " +
            "WHERE a.entity = :entity " +
            "AND DATE(a.datetime) BETWEEN :startDate AND :endDate " +
            "AND (LOWER(COALESCE(u.fullName, '')) LIKE LOWER(CONCAT('%', COALESCE(:fullName, ''), '%')))")
    Page<Audit> findByEntityAndDateRangeAndOptionalFullName(@Param("entity") String entity,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate,
                                                            @Param("fullName") String fullName,
                                                            Pageable pageable);

    Page<Audit> findAllByOrderByDatetimeAsc(Pageable pageable);
}
