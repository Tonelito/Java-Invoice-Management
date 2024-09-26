package com.is4tech.invoicemanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @NotEmpty(message = "Name is required")
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    
    @NotEmpty(message = "Email is required")
    @Column(name = "email", length = 75, nullable = false)
    private String email;
    
    @NotEmpty(message = "Password is required")
    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "status")
    private Boolean status = true;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private Profile profile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return profile.getRoles().stream()
                .map(profileRoleDetail -> new SimpleGrantedAuthority(profileRoleDetail.getRole().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }
}

