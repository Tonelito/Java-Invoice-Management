package com.is4tech.invoicemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.is4tech.invoicemanagement.model.Profile;

@Repository
public interface ProfileRespository extends JpaRepository<Profile, Integer> {
}
