package com.is4tech.invoicemanagement.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.repository.ProfileRoleDetailRepository;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.service.ProfileRoleDetailService;
import com.is4tech.invoicemanagement.service.ProfileService;
import com.is4tech.invoicemanagement.service.RolService;

class ProfileRoleDetailServiceTest {

    @Mock
    private ProfileRoleDetailRepository prdRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private RolService rolService;

    @Mock
    private AuditService auditService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ProfileRoleDetailService profileRoleDetailService;

    private int id = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveProfileRoleDetail() {
        ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
            .profileId(1)
            .roleId(1)
            .build();

        ProfileDto profileDto = ProfileDto.builder()
            .profileId(1)
            .name("Admin")
            .description("Admin Profile")
            .build();

        RolDto rolDto = RolDto.builder()
            .rolId(1)
            .name("ROLE_USER")
            .code("USER")
            .status(true)
            .build();

        Profile profile = Profile.builder()
            .profileId(1)
            .name("Admin")
            .description("Admin Profile")
            .build();

        Rol role = Rol.builder()
            .rolId(1)
            .name("ROLE_USER")
            .code("USER")
            .status(true)
            .build();

        ProfileRoleDetail profileRoleDetail = ProfileRoleDetail.builder()
            .id(ProfileRoleDetailId.builder()
                .profileId(profileRoleDetailDtoId.getProfileId())
                .roleId(profileRoleDetailDtoId.getRoleId())
                .build())
            .profile(profile)
            .role(role)
            .build();

        when(profileService.existsById(eq(id))).thenReturn(true);
        when(rolService.existById(eq(id))).thenReturn(true);
        
        when(profileService.findByIdProfile(eq(id))).thenReturn(profileDto);
        when(rolService.findByIdRol(eq(id))).thenReturn(rolDto);
        when(prdRepository.save(any(ProfileRoleDetail.class))).thenReturn(profileRoleDetail);

        profileRoleDetailService.saveProfileRoleDetail(profileRoleDetailDtoId, request);

    }


    @Test
    void testSaveProfileRoleDetail_ProfileNotFound() {
        ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
            .profileId(1)
            .roleId(1)
            .build();

        when(profileService.findByIdProfile(1)).thenThrow(new ResourceNorFoundException("Profile", "profileId", "1"));

        assertThrows(ResourceNorFoundException.class,
            () -> profileRoleDetailService.saveProfileRoleDetail(profileRoleDetailDtoId, request));
    }

    @Test
    void testFindByIdProfileRoleDetail() {
        Profile profile = Profile.builder()
            .profileId(1)
            .name("Admin")
            .description("Admin Profile")
            .build();

        Rol role = Rol.builder()
            .rolId(1)
            .name("ROLE_USER")
            .code("USER")
            .status(true)
            .build();

        ProfileRoleDetailId profileRoleDetailId = ProfileRoleDetailId.builder()
            .profileId(1)
            .roleId(1)
            .build();

        ProfileRoleDetail profileRoleDetail = ProfileRoleDetail.builder()
            .id(profileRoleDetailId)
            .profile(profile)
            .role(role)
            .build();

        when(prdRepository.findById(profileRoleDetailId)).thenReturn(Optional.of(profileRoleDetail));

        profileRoleDetailService.finByIdProfileRoleDetail(profileRoleDetailId);

        verify(prdRepository, times(1)).findById(profileRoleDetailId);
    }
}
