package com.is4tech.invoicemanagement.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.repository.ProfileRoleDetailRepository;
import com.is4tech.invoicemanagement.utils.MessagePage;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ProfileRoleDetailService {

    private static final String NAME_ENTITY = "Profile Role Detail";
    private static final String NAME_ENTITY_PROFILE = "Profile";
    private static final String NAME_ENTITY_ROL = "Rol";
    private static final String ID_ENTITY = "profile_rol_detail_id";

    @Autowired
    private AuditService auditService;

    private final ProfileRoleDetailRepository prdRespository;
    private final ProfileService profileService;
    private final RolService rolService;

    public ProfileRoleDetailService(ProfileRoleDetailRepository prdRespository, ProfileService profileService,
            RolService rolService) {
        this.prdRespository = prdRespository;
        this.profileService = profileService;
        this.rolService = rolService;
    }

    public MessagePage listAllProfileRolDetail(Pageable pageable, HttpServletRequest request) {
        Page<ProfileRoleDetail> profileDetails = prdRespository.findAll(pageable);

        if (profileDetails.isEmpty()) {
            int statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], null, statusCode, "ProfileRolDetail", request);
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, pageable.toString());
        }

        Map<Profile, List<Rol>> groupedProfileRoles = new HashMap<>();
        for (ProfileRoleDetail detail : profileDetails) {
            groupedProfileRoles
                    .computeIfAbsent(detail.getProfile(), k -> new ArrayList<>())
                    .add(detail.getRole());
        }

        List<ProfileRoleDetailDto> profileRoleDetailDtos = new ArrayList<>();
        for (Map.Entry<Profile, List<Rol>> data : groupedProfileRoles.entrySet()) {
            ProfileDto profileDto = ProfileDto.builder()
                    .profileId(data.getKey().getProfileId())
                    .name(data.getKey().getName())
                    .description(data.getKey().getDescription())
                    .build();

            profileRoleDetailDtos.add(new ProfileRoleDetailDto(
                    profileDto, data.getValue().stream().map(this::toDtoRol).toList()));
        }

        Page<ProfileRoleDetailDto> list = new PageImpl<>(profileRoleDetailDtos, pageable,
                profileDetails.getTotalElements());

        int statusCode = HttpStatus.OK.value();
        auditService.logAudit(profileDetails.getContent(), this.getClass().getMethods()[0], null, statusCode,
                "ProfileRolDetail", request);

        return MessagePage.builder()
                .note("Profile Rol Detail Retrieved Successfully")
                .object(list.getContent())
                .totalElements((int) list.getTotalElements())
                .totalPages(list.getTotalPages())
                .currentPage(list.getNumber())
                .pageSize(list.getSize())
                .build();
    }

    @Transactional
    public ProfileRoleDetailDto saveProfileRoleDetail(ProfileRoleDetailDtoId profileRoleDetailDtoId, HttpServletRequest request) {
        try {
            if (!(profileService.existsById(profileRoleDetailDtoId.getProfileId()))) {
                throw new ResourceNorFoundException(NAME_ENTITY_PROFILE, ID_ENTITY,
                        profileRoleDetailDtoId.getProfileId().toString());
            } else if (!(rolService.existById(profileRoleDetailDtoId.getRoleId()))) {
                throw new ResourceNorFoundException(NAME_ENTITY_ROL, ID_ENTITY,
                        profileRoleDetailDtoId.getRoleId().toString());
            }

            Profile profile = toModelProfile(profileService.findByIdProfile(profileRoleDetailDtoId.getProfileId(), request));
            Rol role = toModelRol(rolService.findByIdRol(profileRoleDetailDtoId.getRoleId(), request));
            ProfileRoleDetail profileRoleDetail = ProfileRoleDetail.builder()
                    .id(ProfileRoleDetailId.builder()
                            .profileId(profileRoleDetailDtoId.getProfileId())
                            .roleId(profileRoleDetailDtoId.getRoleId())
                            .build())
                    .profile(profile)
                    .role(role)
                    .build();

            int statusCode = HttpStatus.CREATED.value();
            auditService.logAudit(profileRoleDetail, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

            return toDto(prdRespository.save(profileRoleDetail));
        } catch (Exception e) {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(profileRoleDetailDtoId, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public ProfileRoleDetailDto finByIdProfileRoleDetail(ProfileRoleDetailId id, HttpServletRequest request) {
        ProfileRoleDetailDto profileRoleDetailDto = prdRespository.findById(id)
            .map(this::toDtoRols)
            .orElseThrow(() -> new ResourceNorFoundException("Role not found with ID: " + id));

        int statusCode = HttpStatus.OK.value();
        auditService.logAudit(profileRoleDetailDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

        return profileRoleDetailDto;
    }

    @Transactional
    public void deleteProfileRolDetailByIds(Integer profileId, Integer roleId, HttpServletRequest request) {
        ProfileRoleDetailDtoId profileRoleDetailDtoId = null;
        try{
            profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
                .profileId(profileId)
                .roleId(roleId)
                .build();
            prdRespository.deleteByProfileIdAndRoleId(profileId, roleId);
            int statusCode = HttpStatus.NO_CONTENT.value();
            auditService.logAudit(profileRoleDetailDtoId, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
        } catch (ResourceNorFoundException e) {
            int statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(profileRoleDetailDtoId, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
            throw e;

        } catch (Exception e) {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(profileRoleDetailDtoId, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    public boolean existByIdProfileRolDetail(ProfileRoleDetailId id) {
        return prdRespository.existsById(id);
    }

    public List<Integer> existByIdProfileRolNotIncluidesDetail(Integer profileId,
            ProfileRoleDetailDtoId profileRoleDetailDtoId, HttpServletRequest request) {
        List<Integer> rolsId = prdRespository.findByIdProfile(profileId);
        List<Integer> rolsIdCopy = new ArrayList<>(rolsId);
        rolsIdCopy.removeAll(profileRoleDetailDtoId.getRols());
        return rolsIdCopy;
    }

    public List<ProfileRoleDetail> findByIdProfileRol(Integer profileId, HttpServletRequest request) {
        List<ProfileRoleDetail> profileRoleDetailDto = prdRespository.findByIdProfileObject(profileId);

        int statusCode = HttpStatus.OK.value();
        auditService.logAudit(profileRoleDetailDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);
        return profileRoleDetailDto;
    }

    private ProfileRoleDetailDto toDto(ProfileRoleDetail profileRoleDetail) {
        ProfileDto profileDto = toDtoProfile(profileRoleDetail);
        return new ProfileRoleDetailDto(profileDto, new ArrayList<>());
    }

    private ProfileRoleDetailDto toDtoRols(ProfileRoleDetail profileRoleDetail) {
        List<RolDto> roles = new ArrayList<>();
        roles.add(toDtoRol(profileRoleDetail.getRole()));
        ProfileDto profileDto = ProfileDto.builder()
                .profileId(profileRoleDetail.getProfile().getProfileId())
                .name(profileRoleDetail.getProfile().getName())
                .description(profileRoleDetail.getProfile().getDescription())
                .build();

        return ProfileRoleDetailDto.builder()
                .profile(profileDto)
                .roles(roles)
                .build();
    }

    private RolDto toDtoRol(Rol rol) {
        return RolDto.builder()
                .rolId(rol.getRolId())
                .name(rol.getName())
                .code(rol.getCode())
                .status(rol.getStatus())
                .build();
    }

    private Rol toModelRol(RolDto rolDto) {
        return Rol.builder()
                .rolId(rolDto.getRolId())
                .name(rolDto.getName())
                .code(rolDto.getCode())
                .status(rolDto.getStatus())
                .build();
    }

    private ProfileDto toDtoProfile(ProfileRoleDetail profileRoleDetail) {
        return ProfileDto.builder()
                .profileId(profileRoleDetail.getProfile().getProfileId())
                .name(profileRoleDetail.getProfile().getName())
                .description(profileRoleDetail.getProfile().getDescription())
                .build();
    }

    private Profile toModelProfile(ProfileDto profileDto) {
        return Profile.builder()
                .profileId(profileDto.getProfileId())
                .name(profileDto.getName())
                .description(profileDto.getDescription())
                .build();
    }
}
