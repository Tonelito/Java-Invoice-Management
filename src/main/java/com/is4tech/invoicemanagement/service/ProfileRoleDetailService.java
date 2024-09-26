package com.is4tech.invoicemanagement.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.repository.ProfileRoleDetailRepository;

@Service
public class ProfileRoleDetailService {

    private static final String NAME_ENTITY_PROFILE = "Profile";
    private static final String NAME_ENTITY_ROL = "Rol";
    private static final String ID_ENTITY = "profile_rol_detail_id";

    @Autowired
    private ProfileRoleDetailRepository prdRespository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private RolService rolService;

    public List<ProfileRoleDetailDto> listAllProfileRolDetail(Pageable pageable) {
        List<ProfileRoleDetail> profileRoleDetails = prdRespository.findAll(pageable).stream().toList();

        Map<Profile, List<Rol>> groupedProfileRoles = new HashMap<>();
        for (ProfileRoleDetail detail : profileRoleDetails) {
            groupedProfileRoles.computeIfAbsent(detail.getProfile(), k -> new ArrayList<>());
            groupedProfileRoles.get(detail.getProfile()).add( detail.getRole());
        }

        List<ProfileRoleDetailDto> profileRoleDetailDtos = new ArrayList<>();
        for (Map.Entry<Profile, List<Rol>> data : groupedProfileRoles.entrySet()) {
            profileRoleDetailDtos.add(new ProfileRoleDetailDto(data.getKey(), data.getValue()));
        }

        return profileRoleDetailDtos;
    }

    @Transactional
    public ProfileRoleDetailDto saveProfileRoleDetail(ProfileRoleDetailDtoId profileRoleDetailDtoId) {
        if(!(profileService.existById(profileRoleDetailDtoId.getProfileId()))) {
            throw new ResourceNorFoundException(NAME_ENTITY_PROFILE, ID_ENTITY, profileRoleDetailDtoId.getProfileId().toString());
        } else if(!(rolService.existById(profileRoleDetailDtoId.getRoleId()))) {
            throw new ResourceNorFoundException(NAME_ENTITY_ROL, ID_ENTITY, profileRoleDetailDtoId.getRoleId().toString());
        }
        Profile profile = profileService.finByIdProfile(profileRoleDetailDtoId.getProfileId());
        Rol role = rolService.findByIdRol(profileRoleDetailDtoId.getRoleId());

        ProfileRoleDetail profileRoleDetail = ProfileRoleDetail.builder()
            .id(ProfileRoleDetailId.builder()
                .profileId(profileRoleDetailDtoId.getProfileId())
                .roleId(profileRoleDetailDtoId.getRoleId())
                .build())
            .profile(profile)
            .role(role)
            .build();
            
        return this.toDto(prdRespository.save(profileRoleDetail));
    }

    @Transactional(readOnly = true)
    public ProfileRoleDetailDto finByIdProfileRoleDetail(ProfileRoleDetailId id) {
        return prdRespository.findById(id)
            .map(this::toDtoRols)
            .orElse(null);
    }

    @Transactional
    public void deleteProfileRolDetailByIds(Integer profileId, Integer roleId) {
        prdRespository.deleteByProfileIdAndRoleId(profileId, roleId);
    }

    public boolean existByIdProfileRolDetail(ProfileRoleDetailId id) {
        return prdRespository.existsById(id);
    }

    public List<Integer> existByIdProfileRolNotIncluidesDetail(Integer profileId, ProfileRoleDetailDtoId profileRoleDetailDtoId){
        List<Integer> rolsId = prdRespository.findByIdProfile(profileId);
        List<Integer> rolsIdCopy = new ArrayList<>(rolsId);
        rolsIdCopy.removeAll(profileRoleDetailDtoId.getRols());
        return rolsIdCopy;
    }

    private ProfileRoleDetailDto toDto(ProfileRoleDetail profileRoleDetail) {
        return new ProfileRoleDetailDto(profileRoleDetail.getProfile(), new ArrayList<>());
    }

    private ProfileRoleDetailDto toDtoRols(ProfileRoleDetail profileRoleDetail) {
        List<Rol> roles = new ArrayList<>();
        roles.add(profileRoleDetail.getRole());
    
        return ProfileRoleDetailDto.builder()
            .profile(profileRoleDetail.getProfile())
            .roles(roles)
            .build();
    }
}
