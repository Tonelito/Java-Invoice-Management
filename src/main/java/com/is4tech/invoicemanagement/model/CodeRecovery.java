package com.is4tech.invoicemanagement.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "code_recovery")
public class CodeRecovery {
    
    @Id
    @Column(name = "code_recovery_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codeRecoveryId;

    @Column(name = "code")
    private String code;
    
    @Column(name = "expiration_date")
    private Date expirationDate;
}
