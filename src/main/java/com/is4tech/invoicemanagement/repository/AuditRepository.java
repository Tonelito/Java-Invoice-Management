package com.is4tech.invoicemanagement.repository;

import com.is4tech.invoicemanagement.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Integer> {
}
