package com.is4tech.invoicemanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "profile_role_detail")
public class ProfileRoleDetail {

    @EmbeddedId
    private ProfileRoleDetailId id;

    @JsonBackReference
    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @JsonBackReference
    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Rol role;
}