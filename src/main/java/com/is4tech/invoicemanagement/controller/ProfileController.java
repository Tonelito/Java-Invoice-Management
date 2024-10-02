package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.annotation.AuditEntity;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.dto.ProfileRolListDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.Profile;
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
public class ProfileController {

  private final ProfileService profileService;
  private final RolService rolService;
  private final ProfileRoleDetailService profileRoleDetailService;

  public ProfileController(ProfileService profileService, RolService rolService,
      ProfileRoleDetailService profileRoleDetailService) {
    this.profileService = profileService;
    this.rolService = rolService;
    this.profileRoleDetailService = profileRoleDetailService;
  }

  private static final String NAME_ENTITY = "Profile";
  private static final String ID_ENTITY = "profile_id";

  @PostMapping("/profile")
  @AuditEntity(NAME_ENTITY)
  public ResponseEntity<Message> saveProfile(@RequestBody @Valid ProfileDto profileDto){
    Profile profileSave = null;
    try {
      profileDto.setStatus(true);
      profileSave = profileService.saveProfile(profileDto);

      ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
          .profileId(profileSave.getProfileId())
          .rols(profileDto.getRolsId())
          .build();

      List<RolDto> rols = new ArrayList();
      for (Integer roldId : profileDto.getRolsId()) {
        savedRolId(profileRoleDetailDtoId);
        rols.add(toRol(rolService.findByIdRol(roldId)));
      }

      return new ResponseEntity<>(Message.builder()
          .note("Saved successfully")
          .object(ProfileRolListDto.builder()
              .profileId(profileSave.getProfileId())
              .name(profileSave.getName())
              .description(profileSave.getDescription())
              .status(profileSave.getStatus())
              .rolsId(rols)
              .build())
          .build(),
          HttpStatus.CREATED);
    } catch (DataAccessException e) {
      throw new BadRequestException("Error save record: " + e.getMessage());
    }
  }

  @PutMapping("/profile/{id}")
  public ResponseEntity<Message> updateProfile(@RequestBody ProfileDto profileDto, @PathVariable Integer id) {
    Profile profileUpdate = null;
    try {
      if (profileService.existById(id)) {
        profileDto.setProfileId(id);
        profileDto.setStatus(true);

        profileUpdate = profileService.saveProfile(profileDto);

        ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
            .profileId(id)
            .rols(profileDto.getRolsId())
            .build();

        List<RolDto> rols = new ArrayList();
        for (Integer roldId : profileDto.getRolsId()) {
          savedRolId(profileRoleDetailDtoId);
          rols.add(toRol(rolService.findByIdRol(roldId)));
        }

        return new ResponseEntity<>(Message.builder()
            .note("Update successfully")
            .object(ProfileRolListDto.builder()
                .profileId(profileUpdate.getProfileId())
                .name(profileUpdate.getName())
                .description(profileUpdate.getDescription())
                .status(profileUpdate.getStatus())
                .rolsId(rols)
                .build())
            .build(),
            HttpStatus.OK);
      } else
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
    } catch (DataAccessException e) {
      throw new BadRequestException("Error update record: " + e.getMessage());
    }
  }

  @PatchMapping("/profile/{id}")
  public ResponseEntity<Message> statusChangeProfile(@PathVariable Integer id) {
    try {
      if (profileService.existById(id)) {
        ProfileDto profileUpdate = profileService.finByIdProfile(id);
        if (profileUpdate.getStatus()) {
          profileUpdate.setStatus(false);
        } else
          profileUpdate.setStatus(true);

        profileService.saveProfile(profileUpdate);

        return new ResponseEntity<>(Message.builder()
            .note("Update successfully")
            .object(profileUpdate)
            .build(),
            HttpStatus.OK);
      } else
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
    } catch (DataAccessException e) {
      throw new BadRequestException("Error update record: " + e.getMessage());
    }
  }

  @DeleteMapping("/profile/{id}")
  public ResponseEntity<Message> deleteProfile(@PathVariable Integer id) {
    try {
      if(profileService.existById(id)){
        ProfileDto profileDelete = profileService.finByIdProfile(id);
        List<ProfileRoleDetail> profileRoleDetail = profileRoleDetailService.findByIdProfileRol(id);

        List<Integer> rolsId = new ArrayList<>();
        for (ProfileRoleDetail profilerRoleDetail : profileRoleDetail) {
          rolsId.add(profilerRoleDetail.getRole().getRolId());
        }
        for (Integer rolsIdModific : rolsId) {
          profileRoleDetailService.deleteProfileRolDetailByIds(id, rolsIdModific);
        }
        profileService.deleteProfile(profileDelete);
      }else
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
      return new ResponseEntity<>(Message.builder()
          .object(null)
          .build(),
          HttpStatus.NO_CONTENT);
    } catch (DataAccessException e) {
      throw new BadRequestException("Error deleting record: " + e.getMessage());
    }
  }

  @GetMapping("/profile/{id}")
  public ResponseEntity<Message> showByIdProfile(@PathVariable Integer id) {
    ProfileDto profileDto = profileService.finByIdProfile(id);
    if (profileDto == null)
      throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());

    return new ResponseEntity<>(Message.builder()
        .note("Record found")
        .object(ProfileDto.builder()
            .profileId(profileDto.getProfileId())
            .name(profileDto.getName())
            .description(profileDto.getDescription())
            .status(profileDto.getStatus())
            .build())
        .build(),
        HttpStatus.OK);
  }

  @GetMapping("/profiles")
  public ResponseEntity<Message> showAllProfiles(@PageableDefault(size = 10) Pageable pageable) {
    Page<Profile> profiles = profileService.listAllProfile(pageable);
    if (profiles.isEmpty())
      throw new ResourceNorFoundException(NAME_ENTITY);

    return new ResponseEntity<>(Message.builder()
        .note("Records found")
        .object(profiles.getContent())
        .build(),
        HttpStatus.OK);
  }

  private void savedRolId(ProfileRoleDetailDtoId profileRoleDetailDtoId) {
    Integer profileId = profileRoleDetailDtoId.getProfileId();
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
