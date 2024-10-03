package com.is4tech.invoicemanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.service.ProfileRoleDetailService;
import com.is4tech.invoicemanagement.service.ProfileService;
import com.is4tech.invoicemanagement.service.RolService;
import com.is4tech.invoicemanagement.utils.Message;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice-management/v0.1/")
public class ProfileRolDetailController {

    private static final String ID_ENTITY = "profile_rol_detail_id";

    private final ProfileRoleDetailService profileRoleDetailService;
    private final ProfileService profileService;
    private final RolService rolService;

    public ProfileRolDetailController(ProfileRoleDetailService profileRoleDetailService, ProfileService profileService,
            RolService rolService) {
        this.profileRoleDetailService = profileRoleDetailService;
        this.profileService = profileService;
        this.rolService = rolService;
    }

    private static final String NAME_ENTITY = "ProfileRolDetail";

    @PostMapping("/profile-rol-detail")
    public ResponseEntity<Message> saveProfileRolDetail(
            @RequestBody @Valid ProfileRoleDetailDtoId profileRoleDetailDtoId) throws BadRequestException {
        try {
            Integer profileId = profileRoleDetailDtoId.getProfileId();
            ProfileDto profileDto = profileService.finByIdProfile(profileId);
            List<Integer> rolsId = profileRoleDetailService.existByIdProfileRolNotIncluidesDetail(profileId,
                    profileRoleDetailDtoId);

            if (!(rolsId.isEmpty())) {
                for (Integer rolsIdModific : rolsId) {
                    profileRoleDetailService.deleteProfileRolDetailByIds(profileId, rolsIdModific);
                }
            }

            List<RolDto> rolsSaved = new ArrayList<>();
            for (Integer rolId : profileRoleDetailDtoId.getRols()) {
                ProfileRoleDetailId detailId = ProfileRoleDetailId.builder()
                        .profileId(profileId)
                        .roleId(rolId)
                        .build();

                if (!(profileRoleDetailService.existByIdProfileRolDetail(detailId))) {
                    ProfileRoleDetailDtoId detailSave = ProfileRoleDetailDtoId.builder()
                            .profileId(profileId)
                            .roleId(rolId)
                            .build();
                    profileRoleDetailService.saveProfileRoleDetail(detailSave);
                    RolDto rol = toRol(rolService.findByIdRol(rolId));
                    if (rol != null) {
                        rolsSaved.add(rol);
                    }
                }
            }
            ProfileRoleDetailDto profileRoleDetailResponse = ProfileRoleDetailDto.builder()
                    .profile(profileDto)
                    .roles(rolsSaved)
                    .build();

            return new ResponseEntity<>(Message.builder()
                    .note("Saved successfully")
                    .object(profileRoleDetailResponse)
                    .build(), HttpStatus.CREATED);

        } catch (DataAccessException e) {
            throw new BadRequestException("Error save record: " + e.getMessage());
        }
    }

    @GetMapping("/profile-rol-detail/rols/{idProfile}")
    public ResponseEntity<Message> showByIdProfile(@PathVariable Integer idProfile) {
        List<ProfileRoleDetail> profileRoleDetail = profileRoleDetailService.findByIdProfileRol(idProfile);

        if (profileRoleDetail == null || profileRoleDetail.isEmpty()) {
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, idProfile.toString());
        }

        List<RolDto> rols = new ArrayList();
        for (ProfileRoleDetail profilerRoleDetail : profileRoleDetail) {
            rols.add(toRol(profilerRoleDetail.getRole()));
        }

        return new ResponseEntity<>(Message.builder()
                .note("Record found")
                .object(rols)
                .build(),
                HttpStatus.OK);
    }

    @GetMapping("/profile-rol-details")
    public ResponseEntity<Message> showAllProfiles(@PageableDefault(size = 10) Pageable pageable) {
        List<ProfileRoleDetailDto> profileRolDetailsId = profileRoleDetailService.listAllProfileRolDetail(pageable);
        if (profileRolDetailsId.isEmpty())
            throw new ResourceNorFoundException(NAME_ENTITY);

        return new ResponseEntity<>(Message.builder()
                .note("Records found")
                .object(profileRolDetailsId)
                .build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/profile-rol-detail/{id}")
    public ResponseEntity<Message> deleteProfileRolDetail(@PathVariable Integer id) throws BadRequestException {
        try {
            if(profileService.existById(id)){
                List<ProfileRoleDetail> profileRoleDetail = profileRoleDetailService.findByIdProfileRol(id);
                List<Integer> rolsId = new ArrayList<>();
                for (ProfileRoleDetail profilerRoleDetail : profileRoleDetail) {
                rolsId.add(profilerRoleDetail.getRole().getRolId());
                }
                for (Integer rolsIdModific : rolsId) {
                profileRoleDetailService.deleteProfileRolDetailByIds(id, rolsIdModific);
                }    
                return new ResponseEntity<>(Message.builder()
                    .object(null)
                    .build(),
                    HttpStatus.NO_CONTENT);
            }else
                throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
        } catch (DataAccessException e) {
            throw new BadRequestException("Error deleting record: " + e.getMessage());
        }
    }

    private RolDto toRol(Rol rol) {
        return RolDto.builder()
                .rolId(rol.getRolId())
                .name(rol.getName())
                .description(rol.getDescription())
                .status(rol.getStatus())
                .build();
    }
}
