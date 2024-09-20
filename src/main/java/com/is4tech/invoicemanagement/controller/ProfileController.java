package com.is4tech.invoicemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is4tech.invoicemanagement.bo.Profile;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.service.ProfileService;
import com.is4tech.invoicemanagement.utils.Message;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {
  
  @Autowired
  private ProfileService profileService;

  private static final String NAME_ENTITY = "Profile";
  private static final String ID_ENTITY = "Id";

  @PostMapping("/profile")
  public ResponseEntity<Message> saveProfile(@RequestBody @Valid ProfileDto profileDto){
    Profile profileSave = null;
    try {
      profileSave = profileService.saveProfile(profileDto);
      return new ResponseEntity<>(Message.builder()
        .note("Saved successfully")
        .object(ProfileDto.builder()
                .profileId(profileSave.getProfileId())
                .name(profileSave.getName())
                .description(profileSave.getDescription())
                .status(profileDto.getStatus())
                .build())
        .build(),
        HttpStatus.CREATED);
    } catch (DataAccessException e) {
      throw new BadRequestException("Error save record: " + e.getMessage());
    }
  }

  @PutMapping("/profile/{id}")
  public ResponseEntity<Message> updateProfile(@RequestBody ProfileDto profileDto, @PathVariable Integer id){
    Profile profileUpdate = null;
    try {
      if(profileService.existById(id)){
        profileDto.setProfileId(id);
        profileUpdate = profileService.saveProfile(profileDto);
        return new ResponseEntity<>(Message.builder()
                    .note("Update successfully")
                    .object(ProfileDto.builder()
                            .profileId(profileUpdate.getProfileId())
                            .name(profileUpdate.getName())
                            .description(profileUpdate.getDescription())
                            .status(profileDto.getStatus())
                            .build())
                    .build(),
                    HttpStatus.OK);
      }else
        throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
    } catch (DataAccessException e) {
      throw new BadRequestException("Error update record: " + e.getMessage());
    }
  }

  @DeleteMapping("/profile/{id}")
  public ResponseEntity<Message> deleteProfile(@PathVariable Integer id){
    try {
      Profile profileDelete = profileService.finByIdProfile(id);
      profileService.deleteProfile(profileDelete);
      return new ResponseEntity<>(Message.builder()
                                  .object(null)
                                  .build(),
                                  HttpStatus.NO_CONTENT);
    } catch (DataAccessException e) {
      throw new BadRequestException("Error deleting record: " + e.getMessage());
    }
  }

  @GetMapping("/profile/{id}")
  public ResponseEntity<Message> showByIdProfile(@PathVariable Integer id){
    Profile profile = profileService.finByIdProfile(id);
    if(profile == null)
      throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());

    return new ResponseEntity<>(Message.builder()
                                .note("Record found")
                                .object(ProfileDto.builder()
                                        .profileId(profile.getProfileId())
                                        .name(profile.getName())
                                        .description(profile.getDescription())
                                        .status(profile.getStatus())
                                        .build())
                                .build(),
                                HttpStatus.OK);
  }

  @GetMapping("/profiles")
  public ResponseEntity<Message> showAllProfiles(@PageableDefault(size = 10) Pageable pageable){
    Page<Profile> profiles = profileService.listAllProfile(pageable);
    if(profiles.isEmpty())
      throw new ResourceNorFoundException(NAME_ENTITY);

    return new ResponseEntity<>(Message.builder()
                          .note("Records found")
                          .object(profiles.getContent())
                          .build(),
                          HttpStatus.OK);
  }

}
