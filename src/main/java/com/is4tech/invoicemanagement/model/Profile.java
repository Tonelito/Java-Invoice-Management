package com.is4tech.invoicemanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "profile")
public class Profile {

  @Id
  @Column(name = "profile_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer profileId;

  @NotEmpty(message = "Name is required")
  @Column(name = "name")
  private String name;

  @NotEmpty(message = "Description is required")
  @Column(name = "description")
  private String description;

  @Column(name = "status")
  private Boolean status;

  @JsonBackReference
  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<ProfileRoleDetail> roles;
}
