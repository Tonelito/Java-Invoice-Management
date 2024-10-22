package com.is4tech.invoicemanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.is4tech.invoicemanagement.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer>{
    Page<Rol> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
