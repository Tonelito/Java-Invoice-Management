package com.is4tech.invoicemanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"role\"")
public class Rol implements Serializable{

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rolId;

    @NotEmpty(message = "Name is required")
    @Column(name = "name")
    private String name;
    
    @NotEmpty(message = "Description is required")
    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Boolean status;

    @JsonBackReference
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProfileRoleDetail> profiles;
}
