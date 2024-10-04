package com.is4tech.invoicemanagement.repository;

import com.is4tech.invoicemanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
