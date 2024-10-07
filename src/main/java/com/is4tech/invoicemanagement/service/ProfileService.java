package com.is4tech.invoicemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.repository.ProfileRespository;
import com.is4tech.invoicemanagement.utils.MessagePage;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ProfileService {

  private final ProfileRespository profileRespository;
  @Autowired
  private AuditService auditService;

  private static final String NAME_ENTITY = "Profile";
  private static final String ID_ENTITY = "profile_id";

  public ProfileService(ProfileRespository profileRespository) {
    this.profileRespository = profileRespository;
  }

  @Transactional
  public MessagePage listAllProfile(Pageable pageable, HttpServletRequest request) {
    Page<Profile> profiles = profileRespository.findAll(pageable);

    if (profiles.isEmpty()) {
      int statusCode = HttpStatus.NOT_FOUND.value();
      auditService.logAudit(null, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);
      throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, pageable.toString());
    }

    int statusCode = HttpStatus.OK.value();
    auditService.logAudit(profiles.getContent(), this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);
    
    return MessagePage.builder()
        .note("Profiles Retrieved Successfully")
        .object(profiles.getContent().stream().map(this::toDto).toList())
        .totalElements((int) profiles.getTotalElements())
        .totalPages(profiles.getTotalPages())
        .currentPage(profiles.getNumber())
        .pageSize(profiles.getSize())
        .build();
  }

  @Transactional
  public ProfileDto saveProfile(ProfileDto profileDto, HttpServletRequest request) {
    try {
      Profile profile = toModel(profileDto);
      Profile savedProfile = profileRespository.save(profile);

      int statusCode = HttpStatus.CREATED.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

      return toDto(savedProfile);
    } catch (Exception e) {
      int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
      throw e;
    }
  }

  @Transactional(readOnly = true)
  public ProfileDto findByIdProfile(Integer id, HttpServletRequest request) {
    ProfileDto profileDto = profileRespository.findById(id)
        .map(this::toDto)
        .orElseThrow(() -> new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, String.valueOf(id)));

    int statusCode = HttpStatus.OK.value();
    auditService.logAudit(profileDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

    return profileDto;
  }

  @Transactional
  public void deleteProfile(ProfileDto profileDto, HttpServletRequest request) {
    try {
      Profile profile = profileRespository.findById(profileDto.getProfileId())
        .orElseThrow(() -> new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, String.valueOf(profileDto.getProfileId())));

      profileRespository.delete(profile);

      int statusCode = HttpStatus.NO_CONTENT.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);
    } catch (ResourceNorFoundException e) {
      int statusCode = HttpStatus.NOT_FOUND.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
      throw e;
    } catch (Exception e) {
      int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
      throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
    }
  }

  @Transactional
  public ProfileDto updateProfile(Integer id, ProfileDto profileDto, HttpServletRequest request) {
    if (profileDto.getProfileId() == null) {
      throw new BadRequestException("Profile ID cannot be null");
    }

    try {
      Profile existingProfile = profileRespository.findById(id)
              .orElseThrow(() -> new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, String.valueOf(id)));

      if (profileDto.getName() != null) {
          existingProfile.setName(profileDto.getName());
      }
      if (profileDto.getDescription() != null) {
          existingProfile.setDescription(profileDto.getDescription());
      }

      Profile savedProfile = profileRespository.save(existingProfile);

      int statusCode = HttpStatus.OK.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

      return toDto(savedProfile);
    } catch (Exception e) {
      int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      auditService.logAudit(profileDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
      throw new BadRequestException("Error updating profile: " + e.getMessage());
    }
  }

  public boolean existsById(Integer id) {
    return profileRespository.existsById(id);
  }

  private ProfileDto toDto(Profile profile) {
    return ProfileDto.builder()
        .profileId(profile.getProfileId())
        .name(profile.getName())
        .description(profile.getDescription())
        .status(profile.getStatus())
        .build();
  }

  private Profile toModel(ProfileDto profileDto) {
    return Profile.builder()
        .profileId(profileDto.getProfileId())
        .name(profileDto.getName())
        .description(profileDto.getDescription())
        .status(profileDto.getStatus())
        .build();
  }
}
