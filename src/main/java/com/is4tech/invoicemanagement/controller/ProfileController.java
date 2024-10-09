package com.is4tech.invoicemanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.is4tech.invoicemanagement.dto.NameSearchDto;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.dto.ProfileRolListDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.service.ProfileRoleDetailService;
import com.is4tech.invoicemanagement.service.ProfileService;
import com.is4tech.invoicemanagement.service.RolService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.MessagePage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice-management/v0.1/profile")
public class ProfileController {

  private final ProfileService profileService;
  private final RolService rolService;
  private final ProfileRoleDetailService profileRoleDetailService;
  @Autowired
  private AuditService auditService;

  public ProfileController(ProfileService profileService, RolService rolService,
      ProfileRoleDetailService profileRoleDetailService) {
    this.profileService = profileService;
    this.rolService = rolService;
    this.profileRoleDetailService = profileRoleDetailService;
  }

  private static final String NAME_ENTITY = "Profile";
  private static final String ID_ENTITY = "profile_id";
  int statusCode;

  @PostMapping("/create")
  public ResponseEntity<Message> saveProfile(@RequestBody @Valid ProfileDto profileDto, HttpServletRequest request){
    ProfileDto profileSave = null;
    try {
      profileDto.setStatus(true);
      profileSave = profileService.saveProfile(profileDto,request);

      List<RolDto> rols = savedRols(profileDto, profileSave.getProfileId(),request);

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
      statusCode = HttpStatus.BAD_REQUEST.value();
      throw new BadRequestException("Error saving record: " + e.getMessage());
    } catch (ResourceNorFoundException e) {
      statusCode = HttpStatus.NOT_FOUND.value();
      throw e;
    } catch (Exception e) {
      statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Message> updateProfile(@RequestBody ProfileDto profileDto, @PathVariable Integer id, HttpServletRequest request) {
    ProfileDto profileUpdate = null;
    try {
      if (profileService.existsById(id)) {
        profileDto.setProfileId(id);
        profileDto.setStatus(true);

        profileUpdate = profileService.updateProfile(id, profileDto, request);

        List<RolDto> rols = savedRols(profileDto, id, request);

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
    } catch (DataAccessException exDt) {
      throw new BadRequestException("Error updating record: " + exDt.getMessage());
    } catch (Exception e) {
      throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @PatchMapping("/status-change/{id}")
  public ResponseEntity<Message> statusChangeProfile(@PathVariable Integer id, HttpServletRequest request) {
    try {
      if (profileService.existsById(id)) {
        ProfileDto profileUpdate = profileService.findByIdProfile(id, request);
        if (profileUpdate.getStatus()) {
          profileUpdate.setStatus(false);
        } else
          profileUpdate.setStatus(true);

        profileService.saveProfile(profileUpdate, request);

        return new ResponseEntity<>(Message.builder()
            .note("Update successfully")
            .object(profileUpdate)
            .build(),
            HttpStatus.OK);
      } else
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
    } catch (DataAccessException exDt) {
      throw new BadRequestException("Error updating record: " + exDt.getMessage());
    } catch (Exception e) {
      throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Message> deleteProfile(@PathVariable Integer id, HttpServletRequest request) {
    try {
      if(profileService.existsById(id)){
        ProfileDto profileDelete = profileService.findByIdProfile(id, request);
        profileService.deleteProfile(profileDelete, request);
      }else
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
      return new ResponseEntity<>(Message.builder()
          .object(null)
          .build(),
          HttpStatus.NO_CONTENT);
    } catch (ResourceNorFoundException e) {
      throw new BadRequestException("Rol not found: " + e.getMessage());
    } catch (DataAccessException e) {
      throw new BadRequestException("Error deleting record: " + e.getMessage());
    } catch (Exception e) {
      throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/show-by-id/{id}")
  public ResponseEntity<Message> showByIdProfile(@PathVariable Integer id, HttpServletRequest request) {
    try {
      ProfileDto profileDto = profileService.findByIdProfile(id, request);
      if (profileDto == null)
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
      List<RolDto> rols = getAllRols(id, request);

      return new ResponseEntity<>(Message.builder()
          .note("Record found")
          .object(ProfileRolListDto.builder()
              .profileId(profileDto.getProfileId())
              .name(profileDto.getName())
              .description(profileDto.getDescription())
              .status(profileDto.getStatus())
              .rolsId(rols)
              .build())
          .build(),
          HttpStatus.OK);
    }catch (ResourceNorFoundException e) {
      statusCode = HttpStatus.NOT_FOUND.value();
      auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request );
      throw e;
    } catch (Exception e) {
      statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
      throw new com.is4tech.invoicemanagement.exception.BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @PostMapping("/show-by-name")
  public ResponseEntity<MessagePage> showByNameProfile(@RequestBody NameSearchDto nameSearchDto, Pageable pageable, HttpServletRequest request) {
    try {
      MessagePage profileDto = profileService.findByNameProfile(nameSearchDto, pageable, request);
      statusCode = HttpStatus.OK.value();
      return new ResponseEntity<>(profileDto, HttpStatus.OK);
    }catch (ResourceNorFoundException e) {
      statusCode = HttpStatus.NOT_FOUND.value();
      auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request );
      throw e;
    } catch (Exception e) {
      statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
      throw new com.is4tech.invoicemanagement.exception.BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/show-all")
  public ResponseEntity<Message> showAllProfiles(@PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
    MessagePage profiles = profileService.listAllProfile(pageable, request);
    return new ResponseEntity<>(Message.builder()
        .note("Records found")
        .object(profiles)
        .build(),
        HttpStatus.OK);
  }

  private List<RolDto> savedRols(ProfileDto profileDto, Integer id, HttpServletRequest request){
    ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
      .profileId(id)
      .rols(profileDto.getRolsId())
      .build();

    List<RolDto> rols = new ArrayList();
    for (Integer roldId : profileDto.getRolsId()) {
      savedRolId(profileRoleDetailDtoId, request);
      rols.add(rolService.findByIdRol(roldId, request));
    }
    return rols;
  }

  private void savedRolId(ProfileRoleDetailDtoId profileRoleDetailDtoId, HttpServletRequest request) {
    Integer profileId = profileRoleDetailDtoId.getProfileId();
    List<Integer> rolsId = profileRoleDetailService.existByIdProfileRolNotIncluidesDetail(profileId,
        profileRoleDetailDtoId, request);

    if (!(rolsId.isEmpty())) {
      for (Integer rolsIdModific : rolsId) {
        profileRoleDetailService.deleteProfileRolDetailByIds(profileId, rolsIdModific, request);
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
        profileRoleDetailService.saveProfileRoleDetail(detailSave, request);
        RolDto rol = rolService.findByIdRol(rolId, request);
        if (rol != null) {
          rolsSaved.add(rol);
        }
      }
    }
  }

  private List<RolDto> getAllRols(Integer id, HttpServletRequest request){
    List<ProfileRoleDetail> profileRoleDetail = profileRoleDetailService.findByIdProfileRol(id, request);
    if (profileRoleDetail == null || profileRoleDetail.isEmpty()) {
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
    }
    List<RolDto> rols = new ArrayList();
    for (ProfileRoleDetail profilerRoleDetail : profileRoleDetail) {
        rols.add(toRol(profilerRoleDetail.getRole()));
    }
    return rols;
  }

  private RolDto toRol(Rol rol) {
    return RolDto.builder()
        .rolId(rol.getRolId())
        .name(rol.getName())
        .code(rol.getCode())
        .status(rol.getStatus())
        .build();
  }
}
