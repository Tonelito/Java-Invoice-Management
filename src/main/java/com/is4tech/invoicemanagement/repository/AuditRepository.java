package com.is4tech.invoicemanagement.repository;

import com.is4tech.invoicemanagement.model.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Integer> {
    @Query("SELECT a FROM Audit a WHERE a.entity = :entity AND DATE(a.datetime) = :date")
    Page<Audit> findByEntityAndDate(@Param("entity") String entity, @Param("date") LocalDate date, Pageable pageable);
}
