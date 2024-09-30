package com.is4tech.invoicemanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "audit")
public class Audit {

    @Id
    @Column(name = "audit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditId;

    @Column(name = "entity", length = 25)
    private String entity;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "response_time")
    private Float responseTime;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    @Column(name = "operation")
    private String operation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
