package com.is4tech.invoicemanagement.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;

import jakarta.transaction.Transactional;

public interface ProfileRoleDetailRepository extends JpaRepository<ProfileRoleDetail, ProfileRoleDetailId> {
    @Modifying //Indica que este método ejecutará una consulta de modificación (UPDATE/DELETE).
    @Transactional //Asegura que esta operación se realice dentro de una transacción
    @Query("delete from ProfileRoleDetail prd where prd.id.profileId = :profileId and prd.id.roleId = :roleId")//Define la consulta JPQL personalizada que ejecutará
    void deleteByProfileIdAndRoleId(@Param("profileId") Integer profileId, @Param("roleId") Integer roleId);

    @Transactional
    @Query("select prd.id.roleId from ProfileRoleDetail prd where prd.id.profileId = :profileId")
    List<Integer> findByIdProfile(@Param("profileId") Integer profileId);

    @Query("select prd from ProfileRoleDetail prd where prd.id.profileId = :profileId")
    List<ProfileRoleDetail> findByIdProfileObject(@Param("profileId") Integer profileId);
}
