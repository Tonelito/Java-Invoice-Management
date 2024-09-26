package com.is4tech.invoicemanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Integer profileId;
    @Column(name = "role_id")
    @JsonBackReference
    private Integer roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Compara la referencia de objeto
        if (o == null || getClass() != o.getClass()) return false; // Verifica si el objeto es nulo o no es de la misma clase
        ProfileRoleDetailId that = (ProfileRoleDetailId) o; // Hace un cast a ProfileRoleDetailId
        return Objects.equals(profileId, that.profileId) &&
                Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, roleId); // Genera un código hash basado en los campos
    }
}
