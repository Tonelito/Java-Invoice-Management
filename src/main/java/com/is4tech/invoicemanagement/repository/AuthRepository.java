package com.is4tech.invoicemanagement.repository;

import com.is4tech.invoicemanagement.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}