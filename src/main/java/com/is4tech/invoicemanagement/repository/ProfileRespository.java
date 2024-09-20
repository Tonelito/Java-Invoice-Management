package com.is4tech.invoicemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.is4tech.invoicemanagement.bo.Profile;

public interface ProfileRespository extends JpaRepository<Profile, Integer> {
}
