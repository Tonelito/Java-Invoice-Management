package com.is4tech.invoicemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileRoleDetailId implements Serializable {

    @Column(name = "profile_id")
    private Integer profileId;
    @Column(name = "role_id")
    private Integer roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileRoleDetailId that = (ProfileRoleDetailId) o;
        return Objects.equals(profileId, that.profileId) &&
                Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, roleId);
    }
}
