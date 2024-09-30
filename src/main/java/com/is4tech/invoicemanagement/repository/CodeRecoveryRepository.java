package com.is4tech.invoicemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.is4tech.invoicemanagement.model.CodeRecovery;

@Repository
public interface CodeRecoveryRepository extends JpaRepository<CodeRecovery,Integer>{
    @Query("select cr from CodeRecovery cr where cr.code = :code")
    CodeRecovery findByCode(@Param("code") String code);
}
